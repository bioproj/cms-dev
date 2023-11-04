package com.wangyang.pojo.vo;

import com.wangyang.pojo.dto.SheetDto;
import lombok.Data;

@Data
public class SheetVo extends ContentVO {
    private String css;
    private String js;
//    private String cssContent;
//
//    private String jsContent;
    private Integer categoryId;
    private String cssClass;
    private String picPath;
    private String picThumbPath;
}
