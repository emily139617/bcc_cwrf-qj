package com.itstyle.bcc_cwrf.web;

import com.itstyle.bcc_cwrf.service.DownloadFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: zhengyy
 * @Date: 2020/1/10 14:33
 * @Version 1.0
 */
@Api(tags = "数据下载模块")
@RestController
@RequestMapping("/download")
public class DownloadController {
    private final static Logger log = LoggerFactory.getLogger(DownloadController.class);

    private final DownloadFileService downloadFileService;

    @Autowired
    public DownloadController(DownloadFileService downloadFileService) {
        this.downloadFileService = downloadFileService;
    }

    /**
     * 回报数据下载模块，根据不同的type类型区分逐日、延伸期、月平均和季平均
     * @param map
     */
    @ApiOperation(value = "回报数据下载模块")
    @PostMapping("/hbDownload")
    public void hbDownloadFile(@RequestBody Map<String, Object> map, HttpServletResponse response) {
       log.info("获取的数据：{}", map);
       downloadFileService.downloadHBFile(map, response);
    }

    /**
     * 回算数据下载模块，根据不同的type类型区分逐日、月平均、季平均和年平均
     * @param map
     */
    @ApiOperation(value = "回算数据下载模块")
    @PostMapping("/hsDownload")
    public void hsDownloadFile(@RequestBody Map<String, Object> map, HttpServletResponse response) {
        log.info("获取的数据：{}", map);
        downloadFileService.downloadHSFile(map, response);
    }
}
