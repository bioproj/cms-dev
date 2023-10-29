package com.wangyang.pojo.dto;


import lombok.Data;

import java.util.List;

@Data
public class Toc {
    private String title;
    private String linkPath;
    private List<Toc> children;
}
