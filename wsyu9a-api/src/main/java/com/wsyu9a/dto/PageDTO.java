package com.wsyu9a.dto;

import lombok.Data;

@Data
public class PageDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String searchKey;
} 