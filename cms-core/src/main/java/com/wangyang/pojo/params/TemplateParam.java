package com.wangyang.pojo.params;

import com.wangyang.pojo.enums.TemplateData;
import com.wangyang.pojo.enums.TemplateType;
import lombok.Data;


@Data
public class TemplateParam {
    private TemplateType templateType;
    private String name;
    private String enName;
    private String description;
    private Boolean status;
    private String templateValue;
    private Integer order;
    private Boolean isSystem;
    private String templateContent;
    private TemplateData templateData;
    private Integer articleSize;
    private Integer parentOrder;

}
