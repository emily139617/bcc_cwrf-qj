package com.itstyle.bcc_cwrf.web;

import com.itstyle.bcc_cwrf.common.entity.Result;
import com.itstyle.bcc_cwrf.service.HbExtremeEventIndexService;
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

/**
 * @description:回报极端事件指数
 * @author:QJ
 * @create:2020-01-06 16:43
 */
@Api(tags = "回报极端事件指数模块")
@RestController
@RequestMapping("/jdsjzs")
public class HbExtremeEventIndexController {
    private final static Logger log = LoggerFactory.getLogger(HbExtremeEventIndexController.class);
    private final static String errorMsg = "图片生成异常，请重试!";
    @Autowired
    private HbExtremeEventIndexService hbExtremeEventIndexService;
    @ApiOperation(value = "气候回报数据极端事件指数模块")
    @PostMapping("/submitParameterToGetPicture")
    public Result weatherPicture(@RequestBody Map<String, Object> map){
        log.info("获取的数据：{}", map);
        String resultPath = hbExtremeEventIndexService.submitParameterToGetPicture(map);
        if(StringUtils.isNotBlank(resultPath)){
            return Result.ok(resultPath);
        }else{
            return Result.error(errorMsg);
        }
    }
}
