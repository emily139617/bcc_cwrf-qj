package com.itstyle.bcc_cwrf.service.Impl;

import com.itstyle.bcc_cwrf.common.entity.Result;
import com.itstyle.bcc_cwrf.service.HbExtremeEventIndexService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @description:回报数据极端事件指数模块实现类
 * @author:QJ
 * @create:2020-01-06 16:49
 */
@Service
public class HbExtremeEventIndexServiceImpl implements HbExtremeEventIndexService {
    private static String pictureBasePath = "/home/export/online1/bcc_cwrf/cwrf/picture/jiduan/";
    private static String[] variableName = {"FD", "SU", "ID", "TR", "TXx", "TNx", "TXn", "TNn", "Rnn", "CDD", "CWD", "DTR",
            "Rx1day", "Rx5day", "SDII", "R10", "R20", "Rnn", "PRCPTOT"};
    private final static String sperator = System.getProperty("file.separator");

    @Override
    /**
     * 提交参数到后台，获取图片地址
     */
    /***
     * 提交过来后端接收参数有
     {
     "startDay":"0302",
     "startHr":"00",
     "caseName":"01",
     "obsDataName":"CN_OBS",
     "year":"1991",
     "month":"3",
     "rnn":"15",
     "timeResolution":"monmean",
     "variableName":"FD",
     "dataType":"hb",
     "seasonName":"spring"
     }
     */
    public String submitParameterToGetPicture(Map<String, Object> map) {
        String picturePath = null;
        String startDay = map.get("startDay") + "";
        String startHr = map.get("startHr") + "";
        String caseName = map.get("caseName") + "";
        String obsDataName = map.get("obsDataName") + "";
        String year = map.get("year") + "";
        String month = map.get("month") + "";
        String variableName = map.get("variableName") + "";
        String rnn = map.get("rnn") + "";
        String timeResolution = map.get("timeResolution") + "";
        String dataType = map.get("dataType") + "";
//        判断参数正确性
        Assert.isTrue(startHr.matches("00|06|12|18"), "仅支持00、06、12、18四个时次");
        Assert.isTrue(startDay.matches("0302|0401"), "仅支持0302和0401");
        Assert.isTrue(month.matches("03|04|05|06|07|08|09"), "仅仅支持03-08或者04-09的月份");
//        Assert.isTrue(month.matches("3|4|5|6|7|8|9"), "仅仅支持3-8或者4-9的月份");
//        Assert.isTrue(caseName.matches("C01|C02|C15|C16|C06"),"case名仅支持C01、C02、C15、C16、C06");
        Assert.isTrue(obsDataName.matches("CFSR|ERI|CN05"), "观测数据名字仅支持CFSR、ERI、CN05");
        Assert.isTrue(dataType.matches("hb|hs"), "数据类型仅支持hb、hs");
        Assert.isTrue(timeResolution.matches("monmean|seasonmean"), "数据类型仅支持monmean、seasonmean");

//表示回报
        switch (variableName) {
            /**
             * 第一种FD ID SU TR PRCPTOT
             *
             */
            case "FD":
            case "ID":
            case "SU":
            case "TR":
            case "PRCPTOT":
                //回报
//                /PRCPTOT"+"_"+year+"_allperiod_"+case+"_"+type+"_"+OBSName
                if (dataType.equalsIgnoreCase("hb")) {
                    picturePath = pictureBasePath + dataType + sperator + variableName + "_" + year + "_allperiod_" + caseName + "_"
                            + startDay + startHr + "_" +
                            obsDataName + ".png";
                }
//                SU_1979_allperiod_01_hs_CN05.png
                //回算
                if (dataType.equalsIgnoreCase("hs")) {
                    picturePath = pictureBasePath + dataType + sperator + variableName + "_" + year + "_allperiod_" + caseName
                            + "_hs_" + obsDataName + ".png";
                }
                break;
            case "CDD":
            case "TXx":
            case "TNx":
            case "TXn":
            case "TNn":
            case "DTR":
            case "Rx1day":
            case "Rx5day":
            case "SDII":
            case "R10":
            case "R20":
            case "Rnn":
            case "CWD":
                //回报
//                /PRCPTOT"+"_"+year+"_allperiod_"+case+"_"+type+"_"+OBSName
                if (dataType.equalsIgnoreCase("hb")) {
                    picturePath = pictureBasePath + dataType + sperator + variableName + "_" + year +
                            "_"+dataType+"_C" +
                            caseName + "_"
                            + startDay + startHr + "_mon_" +month.replaceAll("0","")+"_"+
                            obsDataName + ".png";
                }
//                SU_1979_allperiod_01_hs_CN05.png
                //回算
                if (dataType.equalsIgnoreCase("hs")) {
                    picturePath = pictureBasePath + dataType + sperator + variableName + "_" + year + "_allperiod_" + caseName
                            + "_hs_" + obsDataName + ".png";
                }
                break;
            default:
                picturePath = pictureBasePath;
        }

        return picturePath;
    }

    public String submitParameterToGeneratePicture(Map<String, Object> map) {
        String picturePath = null;
        String dataOuputPath = null;
        String modelDataPath = null;
        String startDay = map.get("startDay") + "";
        String startHr = map.get("startHr") + "";
        String caseName = map.get("caseName") + "";
        String obsDataName = map.get("obsDataName") + "";
        String year = map.get("year") + "";
        String month = map.get("month") + "";
        String variableName = map.get("variableName") + "";
        String rnn = map.get("rnn") + "";
        String timeResolution = map.get("timeResolution") + "";
//        判断参数正确性
        Assert.isTrue(startHr.matches("00|06|12|18"), "仅支持00、06、12、18四个时次");
        Assert.isTrue(startDay.matches("0302|0401"), "仅支持0302和0401");
        Assert.isTrue(month.matches("03|04|05|06|07|08|09"), "仅仅支持03-08或者04-09的月份");
//        Assert.isTrue(caseName.matches("C01|C02|C15|C16|C06"),"case名仅支持C01、C02、C15、C16、C06");
        Assert.isTrue(obsDataName.matches("cfsr|eri|cn_obs"), "观测数据名字仅支持cfsr、eri、cn_obs");
//        获取图片地址
        return "lalala";
    }
}
