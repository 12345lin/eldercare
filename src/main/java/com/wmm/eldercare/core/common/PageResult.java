package com.wmm.eldercare.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private Long total;       // 总记录数
    private List<T> list;     // 当前页数据
    private Integer pageNum;  // 当前页码
    private Integer pageSize; // 每页大小
    private Integer pages;    // 总页数
}