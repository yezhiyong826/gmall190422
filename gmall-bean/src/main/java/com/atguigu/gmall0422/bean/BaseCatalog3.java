package com.atguigu.gmall0422.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
@Data
public class BaseCatalog3 implements Serializable {

    @Id
    @Column
    private String id;
    @Column
    private String name;
    @Column
    private String catalog2Id;
}
