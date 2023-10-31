package com.wangyang.common.pojo;

import com.wangyang.common.annotation.QueryField;
import com.wangyang.common.enums.Lang;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Data
public class BaseEntity {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "myid")
    @GenericGenerator(name = "myid", strategy = "com.wangyang.common.support.ManualInsertGenerator")
    private Integer id;
    @Column(columnDefinition = "int default 0")
    private Integer parentId=0;
    @Column(name = "order_",columnDefinition = "int default 0")
    private Integer order=0;


    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate=new Date();

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate=new Date();

    @Column(name = "lang_")
    @QueryField
    private Lang lang=Lang.ZH;

    private Integer langSource;
}
