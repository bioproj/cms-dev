package com.wangyang.pojo.entity;

import com.wangyang.pojo.entity.base.Content;
import lombok.Data;
import javax.persistence.*;
@Entity
@DiscriminatorValue(value = "0")
@Data
public class Article extends Content {


    @Column(name = "likes", columnDefinition = "int default 0")
    private Integer likes=0;
    @Column(name = "visits", columnDefinition = "int default 0")
    private Integer visits=0;
    @Column(name = "comment_num", columnDefinition = "int default 0")
    private Integer commentNum=0;
//    private Boolean haveHtml=true;
    private String summary;
    private String bilibili;
    private String youtube;
    private String video;
    private Boolean isDivision=false;


}
