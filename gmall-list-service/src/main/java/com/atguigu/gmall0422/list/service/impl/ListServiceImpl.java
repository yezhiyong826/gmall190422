package com.atguigu.gmall0422.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall0422.bean.SkuLsInfo;
import com.atguigu.gmall0422.bean.SkuLsParams;
import com.atguigu.gmall0422.bean.SkuLsResult;
import com.atguigu.gmall0422.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListServiceImpl implements ListService{
    //引入操作es的客户端类
    @Autowired
    private JestClient jestClient;


    public static final String ES_TYPE = "SkuInfo0422";

    public static final String ES_INDEX = "gmall0422";

    @Override
    public void saveSkuInfo(SkuLsInfo skuLsInfo) {
    /*
    1.  定义执行的动作 PUT /movie_index/movie/1 {json 字符串}
    2.  执行动作
     */
        Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();

        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        /*
		es -- 查询功能
		1.	先定义dsl 语句
		2.	定义执行的动作 GET movie_chn/movie/_search
		3.	准备执行
		4.	获取执行结果！
		 */
        // 制作dsl 语句
        String query = makeQueryStringForSearch(skuLsParams);
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 计算总页页数，
        SkuLsResult skuLsResult = makeResultForSearch(searchResult,skuLsParams);
        return skuLsResult;
    }
    // 制作动态dsl 语句
    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        //  1.先构建一个查询器 {}
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 构建 query --
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 构建  bool --
        // 判断当前是否有catalog3Id
        if (skuLsParams.getCatalog3Id()!=null && skuLsParams.getCatalog3Id().length()>0){
            // 构建  bool --- filter -- term {"term": {"catalog3Id": "61"}
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",skuLsParams.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }
        // 判断是否有平台属性值Id
        if (skuLsParams.getValueId()!=null && skuLsParams.getValueId().length>0){
            // 循环获取
            for (String valueId : skuLsParams.getValueId()) {
                // // 构建  bool --- filter -- {"term": {"skuAttrValueList.valueId": "83"}}
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        // 判断商品名称
        if (skuLsParams.getKeyword()!=null && skuLsParams.getKeyword().length()>0){
            /*
            {"match": {
                    "skuName": "小米手机"
                  }}
             */
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",skuLsParams.getKeyword());
            // must
            boolQueryBuilder.must(matchQueryBuilder);

            // 设置高亮 获取高亮对象
            HighlightBuilder highlighter = searchSourceBuilder.highlighter();
            highlighter.field("skuName");
            highlighter.preTags("<span style=color:red>");
            highlighter.postTags("</span>");

            // 将设置好的高亮对象放入查询器
            searchSourceBuilder.highlight(highlighter);
        }
        searchSourceBuilder.query(boolQueryBuilder);

        // 排序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        // 设置从第几条开始查询
        int from = (skuLsParams.getPageNo()-1)*skuLsParams.getPageSize();
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(skuLsParams.getPageSize());
        // 聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(groupby_attr);
        // 动态获取dsl
        String query = searchSourceBuilder.toString();
        System.out.println("query:"+query);
        return query;
    }
    // 设置返回结果
    private SkuLsResult makeResultForSearch(SearchResult searchResult, SkuLsParams skuLsParams) {
        SkuLsResult skuLsResult = new SkuLsResult();
        //        List<SkuLsInfo> skuLsInfoList;
        // 创建一个集合来存储SkuLsInfo
        ArrayList<SkuLsInfo> skuLsInfoArrayList = new ArrayList<>();
        // 循环es 中的skuLsInfo
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        if (hits!=null && hits.size()>0){
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo skuLsInfo = hit.source;
                // 发现异常：skuLsInfo的名称并不是高亮！
                if (hit.highlight!=null && hit.highlight.size()>0){
                    // 获取高亮中的key ，取值
                    List<String> list = hit.highlight.get("skuName");
                    // 即为高亮字段
                    String skuNameHI = list.get(0);
                    // 替换原来的skuName
                    skuLsInfo.setSkuName(skuNameHI);
                }
                skuLsInfoArrayList.add(skuLsInfo);
            }
        }
        // 获取es 中的结果
        skuLsResult.setSkuLsInfoList(skuLsInfoArrayList);
//        long total;
        skuLsResult.setTotal(searchResult.getTotal());
//        long totalPages;
        // 10 3 4  | 9 3 3
        // long totalPage = searchResult.getTotal()%skuLsParams.getPageSize()==0?searchResult.getTotal()/skuLsParams.getPageSize():searchResult.getTotal()/skuLsParams.getPageSize()+1;
//        long totalPage1 = Math.ceil()  Math.round()
        long totalPage = (searchResult.getTotal()+skuLsParams.getPageSize()-1)/skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPage);
//        List<String> attrValueIdList;
        // 通过聚合方式来获取平台属性值Id
        // 创建一个List集合来存储平台属性值Id
        ArrayList<String> stringArrayList = new ArrayList<>();
        MetricAggregation aggregations = searchResult.getAggregations();
        // 根据groupby_attr 获取桶的数据
        List<TermsAggregation.Entry> groupby_attr = aggregations.getTermsAggregation("groupby_attr").getBuckets();
        for (TermsAggregation.Entry entry : groupby_attr) {
            String valueId = entry.getKey();
            stringArrayList.add(valueId);
        }
        // 保存平台属性值Id
        skuLsResult.setAttrValueIdList(stringArrayList);
        return skuLsResult;
    }
}