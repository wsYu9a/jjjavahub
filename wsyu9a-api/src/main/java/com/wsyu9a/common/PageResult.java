package com.wsyu9a.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private int pageSize;
    private int pageNum;

    public PageResult(List<T> records, long total, int pageSize, int pageNum) {
        this.records = records;
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }
} 