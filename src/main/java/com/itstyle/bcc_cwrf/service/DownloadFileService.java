package com.itstyle.bcc_cwrf.service;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: zhengyy
 * @Date: 2020/1/10 14:38
 * @Version 1.0
 */
public interface DownloadFileService {
    void downloadHBFile(Map<String, Object> map, HttpServletResponse response);

    void downloadHSFile(Map<String, Object> map, HttpServletResponse response);
}
