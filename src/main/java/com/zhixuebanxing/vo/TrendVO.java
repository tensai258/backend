package com.zhixuebanxing.vo;

import lombok.Data;

/**
 * 学习趋势VO
 */
@Data
public class TrendVO {
    /** 日期 */
    private String date;
    /** 答题数 */
    private Integer totalCount;
    /** 正确数 */
    private Integer correctCount;
    /** 正确率 */
    private Double correctRate;
    /** 当日新错题数 */
    private Integer newWrongCount;
}
