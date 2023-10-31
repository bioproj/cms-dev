package com.wangyang.common.pojo;

import com.wangyang.common.enums.Lang;
import lombok.Data;

import java.util.List;

@Data
public class BaseVo<T> {
    private Integer id;
    private Integer parentId;
    private Integer order;
    private List<T> children;
    private Lang lang=Lang.ZH;
}
