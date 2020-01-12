package com.itstyle.bcc_cwrf.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description:
 * @author:QJ
 * @create:2020-01-06 16:46
 */
@Service
public interface HbExtremeEventIndexService{
    String submitParameterToGetPicture(Map<String,Object> map);
}
