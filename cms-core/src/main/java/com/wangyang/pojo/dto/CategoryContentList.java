package com.wangyang.pojo.dto;

import com.wangyang.pojo.vo.ArticleVO;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.pojo.vo.ContentVO;
import lombok.Data;

import java.util.List;

@Data
public class CategoryContentList<T> {
    private CategoryVO category;
    private List<T> contentVOS;
}
