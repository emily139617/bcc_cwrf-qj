package com.itstyle.bcc_cwrf.service;

import com.itstyle.bcc_cwrf.common.entity.Result;

import java.util.Map;

/**
 * @Description: 气候回报数据 评估检验及评分指标生成模块
 * @Author hy
 * @Date 2020-01-10 16:59
 * @Version 1.0
 */
public interface HbEvaluationService {

    /**
     * 回报空间相关系数txt文件
     * @param map
     * @return
     */
    Result execSccTxt(Map<String, Object> map);


    /**
     * 回报时间相关系数png路径
     * @param map
     * @return
     */
    Result execTccPicture(Map<String, Object> map);
}
