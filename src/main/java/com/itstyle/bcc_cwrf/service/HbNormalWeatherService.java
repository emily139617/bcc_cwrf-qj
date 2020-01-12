package com.itstyle.bcc_cwrf.service;

import com.itstyle.bcc_cwrf.common.entity.Result;

import java.util.List;
import java.util.Map;

/**
 * @Description: 气候回报数据 常规气候预测产品生成模块
 * @Author hy
 * @Date 2019-12-24 14:50
 * @Version 1.0
 */
public interface HbNormalWeatherService {

    /**
     * 气温出图 第一张提交绘图
     * @param map
     * @return
     */
    String weatherFirstPicture(Map<String, Object> map);

    /**
     * 常规气候预测出图  单条执行
     * @param map
     * @return
     */
    List<String> execWeatherFirstPicture(Map<String, Object> map);

    /**
     * 常规气候预测出图  批量执行
     * @param map
     * @return
     */
    List<Map<String, Object>> batchExecWeatherFirstPicture(Map<String, Object> map);
    /**
     * 气温出图 第二张提交绘图
     * @param map
     * @return
     */
    String weatherSecondPicture(Map<String, Object> map);

    /**
     * 气温出图 第三张提交绘图
     * @param map
     * @return
     */
    Result weatherThirdPicture(Map<String, Object> map);

    /**
     * 常规气候预测出图  第三张提交绘图 单条执行
     * @param map
     * @return
     */
    List<String> execWeatherThirdPicture(Map<String, Object> map);

    /**
     * 常规气候预测出图  第三张提交绘图 批量执行
     * @param map
     * @return
     */
    List<Map<String, Object>> batchExecWeatherThirdPicture(Map<String, Object> map);
}
