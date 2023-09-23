package com.wangyang.pojo.entity;

import com.wangyang.pojo.entity.base.BaseTemplate;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "1")
@Data
public class Components extends BaseTemplate {

    private String viewName;
    private String dataName;
    private String dataArgs;

    @Column(name = "template_event")
    private String event;





    public Components(){
    }

    public Components(String name, String path , String templateValue, String viewName, String dataName, String event, Boolean status){
        super.setPath(path);

        super.setName(name);
        super.setTemplateValue(templateValue);
        this.setViewName(viewName);
        this.setDataName(dataName);
        this.setEvent(event);
        this.setStatus(status);
        super.setIsSystem(true);
    }
    public Components(String name, String path , String templateValue, String viewName, String dataName, String event, Boolean status,Boolean isSystem){
       super.setPath(path);

        super.setName(name);
        super.setTemplateValue(templateValue);
        this.setViewName(viewName);
        this.setDataName(dataName);
        this.setEvent(event);
        this.setStatus(status);
        super.setIsSystem(isSystem);
    }

}
