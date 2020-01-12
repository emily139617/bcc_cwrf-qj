package com.itstyle.bcc_cwrf.service.Impl;

import com.itstyle.bcc_cwrf.service.SummerWindIndexService;
import com.itstyle.bcc_cwrf.utils.ReadServiceTxtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.itstyle.bcc_cwrf.utils.ParseShellUtils.parse;

/**
 * @description:
 * @author:QJ
 * @create:2020-01-09 14:24
 */
@Service
public class SummerWindIndexServiceImpl implements SummerWindIndexService {
    private final static Logger log = LoggerFactory.getLogger(SummerWindIndexServiceImpl.class);
    public static String[] obsNames = {"CN05", "ERI", "CFSR"};
    public static String[] obsPaths = {"/GFPS8p/sw_BData/DATA_POOL/CN_OBS/", "/GFPS8p/sw_BData/DATA_POOL/eri/",
            "/GFPS8p/sw_BData/DATA_POOL/cfsr/"};
    public static String[] variableNames = {"summer_monsoon", "winter_monsoon"};
    public static String outputPathBase = "/home/export/online1/bcc_cwrf/cwrf/picture/summerIndex/";

    @Override
    public Map<String, Object> submitParameterToGetTxtData(Map<String, Object> map) {
        //获取参数
        String startDay = map.get("startDay") + "";
        String startHr = map.get("startHr") + "";
        String caseName = map.get("caseName") + "";
        String obsDataName = map.get("obsDataName") + "";
        String month = map.get("month") + "";
        String variableName = map.get("variableName") + "";//传夏季风指数或者冬季风指数
        String dataType = map.get("dataType") + "";
        String startYear = map.get("startYear") + "";
        String endYear = map.get("endYear") + "";
//        判断参数正确性
        Assert.isTrue(startHr.matches("00|06|12|18"), "仅支持00、06、12、18四个时次");
        Assert.isTrue(startDay.matches("0302|0401"), "仅支持0302和0401");
        Assert.isTrue(month.matches("03|04|05|06|07|08|09"), "仅仅支持03-08或者04-09的月份");
//        Assert.isTrue(month.matches("3|4|5|6|7|8|9"), "仅仅支持3-8或者4-9的月份");
//        Assert.isTrue(caseName.matches("C01|C02|C15|C16|C06"),"case名仅支持C01、C02、C15、C16、C06");
        Assert.isTrue(obsDataName.matches("CFSR|ERI|CN05"), "观测数据名字仅支持CFSR、ERI、CN05");
        Assert.isTrue(dataType.matches("hb|hs"), "数据类型仅支持hb、hs");
        Assert.isTrue(variableName.matches("summer_monsoon|winter_monsoon"), "数据类型仅支持hb、hs");
        String outputPath = null;
        String execShell = null;
        String startMon = null;
        String endMon = null;
        if (variableName.equalsIgnoreCase("summer_monsoon")) {
            startMon = "6";
            endMon = "8";
        }
        if (variableName.equalsIgnoreCase("winter_monsoon")) {
            startMon = "0";
            endMon = "2";
        }
        String shellPath = "/home/export/online1/cwrf_pf/qj/CWRF_bcc/zh/index/circu_index/";
        Map<String, Object> result = new HashMap<>();
        outputPath = outputPathBase + dataType + "/";
        //执行脚本
        //variableName variableName obsDataName
        if (variableName.equalsIgnoreCase("summer_monsoon")) {
            if (dataType.equalsIgnoreCase("hb")) {
                //回报的夏季风指数
                //根据观测的不同
                String obsPath = null;
                if (obsDataName.equalsIgnoreCase("CN05")) {
                    obsPath = obsPaths[0];
                } else if (obsDataName.equalsIgnoreCase("eri")) {
                    obsPath = obsPaths[1];
                } else {
                    obsPath = obsPaths[2];
                }
                String logFileName = "log_" + startYear + "-" + endYear + "_" + startDay + startHr + "C" + caseName
                        + "-summer_monsoon" + obsDataName +
                        ".log";
                execShell = shellPath + "./summerHb.sh " + startYear + " " + endYear + " " + caseName + " " + startHr + " " +
                        "" + startDay + " " + obsPath + " " + dataType + " " + outputPath + " " + obsDataName + " " +
                        "" + variableName + " " +
                        ">" + shellPath + logFileName;
                log.info("执行脚本是：" + execShell);
                parse(execShell);
            }
            if (dataType.equalsIgnoreCase("hs")) {
                //回算的夏季风指数
            }
        }
        //根据参数获取txt地址
        String filename = variableName + "_" + startYear + "_" + endYear + "_" + dataType + "_C" + caseName + "_" + startDay + startHr + "_" + startMon + "_" + endMon + ".txt";
        log.info("文件名：" + outputPath + filename);
        result.put("txtPath", outputPath + filename);
        //获取标题
        List<String> title1 = new ArrayList<>();
        List<String> title2 = new ArrayList<>();
        title1.add("Summer Monsoon Index");
        title1.add(obsDataName);
        title2.add(startDay + startHr);
        List<Integer> yearList = new ArrayList<>();
        for (int i = Integer.valueOf(startYear); i <= Integer.valueOf(endYear); i++) {
            yearList.add(i);
        }
        File file = new File(outputPath + filename);
        //当文件存在了
        if (file.exists()) {
            //获取数据
            try {
                Map<String, Object> map1 = ReadServiceTxtUtils.readSummerWindTxt(file);
                result.put("data", map1);
                result.put("year", yearList);
                result.put("title1", title1);
                result.put("title2", title2);
                result.put("yAxis", "");
                parse("rm -rf "+outputPath + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
