package com.wangyang.pojo.vo;

import com.wangyang.pojo.dto.ContentTab;
import lombok.Data;

import java.util.List;

@Data
public class SheetDetailVO extends ContentDetailVO{

    private List<ContentTab> contentTab;
}
