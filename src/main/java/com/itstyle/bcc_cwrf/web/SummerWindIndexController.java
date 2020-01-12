package com.itstyle.bcc_cwrf.web;

import com.itstyle.bcc_cwrf.common.entity.Result;
import com.itstyle.bcc_cwrf.service.HbExtremeEventIndexService;
import com.itstyle.bcc_cwrf.service.SummerWindIndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @description:夏季风指数
 * @author:QJ
 * @create:2020-01-09 14:21
 */
@Api(tags = "夏季风指数生成模块")
@RestController("/summerWindIndex")
public class SummerWindIndexController {
    private final static Logger log = LoggerFactory.getLogger(SummerWindIndexController.class);
    private final static String errorMsg = "夏季风指数生成异常，请重试!";
    @Autowired
    private SummerWindIndexService summerWindIndexService;
    @ApiOperation(value = "夏季风指数模块")
    @PostMapping("/submitParameterToGetTxtData")
    public Result weatherPicture(@RequestBody Map<String, Object> map){
        //返回文件的数据，数据用json包，然后返给result
        log.info("获取的数据：{}", map);
        if (map == null) {
            return Result.error("传参有误");
        }
        else {
            Map<String, Object> result = summerWindIndexService.submitParameterToGetTxtData(map);
            if(result!=null){
                return Result.qjok(result);
            }else{
                return Result.error(errorMsg);
            }
        }
    }
}
