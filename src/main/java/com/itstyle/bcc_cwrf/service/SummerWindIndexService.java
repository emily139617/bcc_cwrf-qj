package com.itstyle.bcc_cwrf.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description:
 * @author:QJ
 * @create:2020-01-09 14:24
 */
@Service
public interface SummerWindIndexService {
    Map<String, Object> submitParameterToGetTxtData(Map<String,Object> map);
}
