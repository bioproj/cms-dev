package com.wangyang.pojo.params;

import lombok.Data;

import java.util.Set;

@Data
public class LiteratureParam {
    private String originalContent;
    private String title;
    private Set<Integer> tagIds;
}
