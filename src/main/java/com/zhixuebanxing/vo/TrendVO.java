package com.zhixuebanxing.vo;

import lombok.Data;

@Data
public class TrendVO {
    private String date;
    private Double score;
    private Integer completedCount;
    private Integer duration;
}
