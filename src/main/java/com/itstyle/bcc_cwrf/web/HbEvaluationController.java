package com.itstyle.bcc_cwrf.web;
/**
 * @Description: 气候回报数据 评估检验及评分指标生成模块
 * @Author hy
 * @Date 2020-01-10 16:55
 * @Version 1.0
 */

import com.itstyle.bcc_cwrf.common.entity.Result;
import com.itstyle.bcc_cwrf.service.HbEvaluationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "气候回报数据 评估检验及评分指标生成模块")
@RestController
@RequestMapping("/hbEvaluation")
public class HbEvaluationController {

    private final static Logger log = LoggerFactory.getLogger(HbNormalWeatherController.class);
    private final static String errorMsg = "图片生成异常，请重试!";

    @Autowired
    private HbEvaluationService hbEvaluationService;

    /**
     * 根据传递参数  生成txt文件  提供前端画图
     * @param map
     * @return
     */
    @ApiOperation(value = "气候回报数据 常规气候预测产品生成模块 空间相关系数")
    @PostMapping("/scc")
    public Result execSccTxt(@RequestBody Map<String, Object> map){
        log.info("获取的数据：{}", map);
        Result resultData = hbEvaluationService.execSccTxt(map);
        return resultData;

    }


    /**
     * 根据前端提交参数  直接出图
     * @param map
     * @return
     */
    @ApiOperation(value = "气候回报数据 常规气候预测产品生成模块 时间相关系数")
    @PostMapping("/tcc")
    public Result execTccPicture(@RequestBody Map<String, Object> map){
        log.info("获取的数据：{}", map);
        Result resultData = hbEvaluationService.execTccPicture(map);
        return resultData;

    }
}
