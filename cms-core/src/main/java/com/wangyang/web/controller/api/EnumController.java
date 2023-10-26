package com.wangyang.web.controller.api;

import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.enums.Lang;
import com.wangyang.interfaces.IEnumList;
import com.wangyang.pojo.enums.NetworkType;
import com.wangyang.pojo.enums.TemplateData;
import com.wangyang.pojo.enums.TemplateType;
import com.wangyang.common.enums.ValueEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enum")
public class EnumController {

    @Autowired(required = false)
    IEnumList enumList;
    @GetMapping
    public ValueEnum[] listName(@RequestParam String name){

        if(name.equals("TemplateType")){
            return TemplateType.values();
        }else if (name.equals("TemplateData")){
            return TemplateData.values();
        } else if (name.equals("Lang")) {
            return Lang.values();
        } else if (name.equals("NetworkType")) {
            return NetworkType.values();
        } else {
            if(enumList!=null){
                return enumList.list(name);
            }
        }
        throw new ObjectException("enum 不存在！");
    }
}
