package com.itstyle.bcc_cwrf.service.Impl;

import com.itstyle.bcc_cwrf.common.entity.Result;
import com.itstyle.bcc_cwrf.service.HsNormalWeatherService;
import com.itstyle.bcc_cwrf.utils.*;
import com.itstyle.bcc_cwrf.web.HbNormalWeatherController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author hy
 * @Description //气候回报数据 常规气候预测产品生成模块
 * @Date
 * @Param
 * @return }
 */
@Service
public class HsNormalWeatherServiceImpl implements HsNormalWeatherService {

    private final static Logger log = LoggerFactory.getLogger(HbNormalWeatherController.class);

    /**
     * 常规气候模块要素
     */
    // 气温要素类型
    private static final String[] weatherVarName = {"AT2M", "AT2M", "T2MAX", "T2MAX", "T2MIN", "T2MIN"};
    // 降水要素类型
    private static final String[] pravgVarName = {"PRAVG", "PRAVG"};
    // 10米风要素类型
    private static final String[] wsavgVarName = {"AU10", "AU10", "AV10", "AV10", "WSAVG", "WSAVG"};
    // 2米比湿要素类型
    private static final String[] otherVarName = {"", ""};
    // 月份数字转英文
    private static final String[] monthEngName = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sept","Oct","Nov","Dec"};
    // 季度数字转英文
    private static final String[] seasEngName = {"MAM", "JJA", "SON", "DJF"};
    // 延伸期，月尺度，季尺度数组
    private static final String[] meanTypeArray = {"monmean", "seasmean", "yearmean"};
    private static final String[] shellMeanTypeArray = {"monmean", "seasmean", "yearmean"};

    // 图片后缀数组  firstPicture
    private static final String[] pictureNameArray = {"anomaly", "ysqmean_bias", "monmean", "monmean_bias", "seasmean",
            "seasmean_bias"};

    // 图片后缀数组  secondPicture
    private static final String[] pictureMoreYearNameArray = {"years_ysqmean", "years_ysqmean_bias", "years_monmean",
            "years_monmean_bias", "years_seasmean", "years_seasmean_bias"};

    // 图片后缀数组  thirdPicture
    private static final String[] pictureAnomalyNameArray = {"ysqmean_anomaly", "ysq_anomaly", "monmean_anomaly",
            "anomaly", "seasmean_anomaly", "anomaly"};

    // 距平出图名称数组
    private static final String[] titleAnomalyNameArray = {"Extended forecast Average anomaly", "Monthly Average anomaly",
            "Seasonal Average anomaly", "Extended forecast Average Precipitation anomaly percentage",
            "Monthly Average Precipitation anomaly percentage", "Seasonal Average Precipitation anomaly percentage"};

    /**
     * obsDataPath\ modelDataPath 路径  使用时需要添加相关信息 区分延伸期，月尺度，季尺度
     */
    private static final String obs_base_path = "/GFPS8p/sw_BData/bcccsmProduct/obs/hb/";
    private static final String model_base_path = "/GFPS8p/sw_BData/bcccsmProduct/model/hb/";
    /**
     * 气温出图 第一张提交绘图
     * @param map
     * @return
     *
     * json参数
     *
     * {
     *     "typeName": 0,
     *     "meanType": 2,
     *     "caseNum": "01",
     *     "obsDataName": "cn_obs",
     *     "month": "03",
     *     "seas": "0",
     *     "varName": "0",
     *     "year": "1993"
     * }
     */
    @Override
    public String weatherFirstPicture(Map<String, Object> map) {
        String resultPath = null;
        List<String> resultList = new ArrayList<String>();
        //类型  气温-0/降水-1/10米风-2/2米比湿-3
        String typeName = map.get("typeName") + "";
        //类别  延伸期-0/月尺度-1/季节尺度-2
        String meanType = map.get("meanType") + "";
        //case
        String caseNum = map.get("caseNum") + "";
        //观测资料 cfsr/eri/cn_obs站点
        String obsDataName = map.get("obsDataName") + "";
        //月尺度对应月份
        String month = map.get("month") + "";
        //季尺度季度
        String seas = map.get("seas") + "";
        //年尺度
        String year = map.get("year") + "";
        //要素 分3种情况 需要根据类别 延伸期-0/月尺度-1/季节尺度-2区分
        //气温：平均气温-0/平均气温偏差-1/平均日最高气温-2/平均日最高气温偏差-3/平均日最低气温-4/平均日最低气温偏差-5
        //降水：平均降水-0/平均降水偏差-1
        //10米风： 纬向风速-0/经向风速-1/平均10米风速-2/纬向风速偏差-3/经向风速偏差-4/平均10米风速偏差-5/
        //2米比湿： 2米比湿-0/2米比湿偏差-1
        //平均气温对应的是AT2M，日最高气温对应的是T2MAX。日最低气温对应的是T2MIN，降水就是PRAVG，风速就是WSAVG
        String varName = map.get("varName") + "";
        //obsDataName名称转换
        if ("CN05".equalsIgnoreCase(obsDataName)){
            obsDataName = "CN05";
        }else if("CFSR".equalsIgnoreCase(obsDataName)){
            obsDataName = "";
        }else if("ERI".equalsIgnoreCase(obsDataName)){
            obsDataName = "ERI";
        }
        //具体路径拼接  需要区分  1.气温 降水 10米风 2米比湿    2.延伸期 月尺度 季节尺度
        //varName 是基数的话 代表偏差
        int varNameIndex = Integer.valueOf(varName);
        if ("0".equals(typeName)){
            //气温
            varName = weatherVarName[varNameIndex];
        }else if ("1".equals(typeName)){
            //降水
            varName = pravgVarName[varNameIndex];
        }else if ("2".equals(typeName)){
            //10米风
            varName = wsavgVarName[varNameIndex];
        }else if ("3".equals(typeName)){
            //2米比湿
            varName = otherVarName[varNameIndex];
        }
        resultList.add(varName);
        resultList.add(year);
        resultList.add(caseNum);
        resultList.add(obsDataName);
        int meanTypeIndex = Integer.valueOf(meanType);
        //区分是否是偏差
        String[] pictureName = {"monmean", "ysqmean_bias", "seasmean", "monmean_bias", "yearmean", "seasmean_bias"};
        //图片添加字段的index
        int count = 0;
        if(varNameIndex % 2 == 0){
            resultPath = HsPathUtils.Hs_Normal_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
        }else {
            count = count + 1;
            resultPath = HsPathUtils.Hs_Normal_dev_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
        }
        if("0".equals(meanType)){
            int index = Integer.valueOf(month);
            resultList.add(2, monthEngName[index]);
            resultList.add(4,pictureName[count]);
            resultPath = resultPath + pathName(resultList);
        }else if("1".equals(meanType)){
            int index = Integer.valueOf(seas);
            count = count + 2;
            resultList.add(2, seasEngName[index]);
            resultList.add(4, pictureName[count]);
            resultPath = resultPath + pathName(resultList);
        }else if("2".equals(meanType)){
            count = count + 4;
            resultList.add(3, pictureName[count]);
            resultPath = resultPath + pathName(resultList);
        }
        return resultPath;
    }

    /**
     * 实际出图执行方法  提交到服务器
     * @param map
     * @return
     *
     * json参数
     *
        {
            "typeName": 0,
            "meanType": 0,
            "mmDd": "0302",
            "hour": [
            "00",
            "06",
            "12",
            "18"
            ],
            "caseNum": [
            "01",
            "02",
            "15",
            "16"
            ],
            "obsDataName": "cn_obs",
            "month": "03",
            "seas": "0",
            "varName": "0",
            "startYear": "1993",
            "endYear": "2015"
        }
     0401的caseNum为01，02，06，15
     */
    @Override
    public List<String> execWeatherFirstPicture(Map<String, Object> map) {

        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        List<String> resultList = new ArrayList<>();
        String typeName = map.get("typeName") + "";
        //类别  延伸期-0/月尺度-1/季节尺度-2
        String meanType = map.get("meanType") + "";
        //起报month+day
        String monthAndDay = map.get("mmDd") + "";
        //起报时次 hour
        String hour = map.get("hour") + "";
        //case
        String caseNum = map.get("caseNum") + "";
        //观测资料 cfsr/eri/cn_obs站点
        String obsDataName = map.get("obsDataName") + "";
        //月尺度对应月份
        String month = map.get("month") + "";
        //季尺度季度
        String seas = map.get("seas") + "";
        String varName = map.get("varName") + "";
        String startYear = map.get("startYear") + "";
        String endYear = map.get("endYear") + "";
        String obsDataPath = null;
        String modelDataPath = null;
        String filterModelPath = null;
        String filterObsPath = null;
        //用于记录obsDataPath内路径名称
        String obsPathName = null;
        //obsDataName名称转换
        if ("CN05".equalsIgnoreCase(obsDataName)){
            obsDataName = "CN05";
            obsPathName = "CN_OBS";
        }else if("CFSR".equalsIgnoreCase(obsDataName)){
            obsDataName = "";
            obsPathName = "";
        }else if("ERI".equalsIgnoreCase(obsDataName)){
            obsDataName = "ERI";
            obsPathName = "ERI";
        }
        //具体路径拼接  需要区分  1.气温 降水 10米风 2米比湿    2.延伸期 月尺度 季节尺度
        int varNameIndex = Integer.valueOf(varName);
        if ("0".equals(typeName)){
            //气温
            varName = weatherVarName[varNameIndex];
        }else if ("1".equals(typeName)){
            //降水
            varName = pravgVarName[varNameIndex];
        }else if ("2".equals(typeName)){
            //10米风
            varName = wsavgVarName[varNameIndex];
        }else if ("3".equals(typeName)){
            //2米比湿
            varName = otherVarName[varNameIndex];
        }

        obsDataPath = obs_base_path + obsPathName + "/";
        modelDataPath = model_base_path + monthAndDay + "/POST_C" + caseNum + "_GCMSST/" + monthAndDay + hour + "/";
        // 根据起报时次mmdd和hour区分出obsDataPath  modelDataPath
        if ("0302".equals(monthAndDay)){
            if("00".equals(hour)){
                obsDataPath = obsDataPath + "0302/";
            }else {
                obsDataPath = obsDataPath + "0303/";
            }
        }else if("0401".equals(monthAndDay)){
            if("00".equals(hour)){
                obsDataPath = obsDataPath + "0401/";
            }else {
                obsDataPath = obsDataPath + "0402/";
            }
        }
        int meanTypeIndex = Integer.valueOf(meanType);
        modelDataPath = modelDataPath + shellMeanTypeArray[meanTypeIndex] + "/";
        obsDataPath = obsDataPath + shellMeanTypeArray[meanTypeIndex] + "/";
        log.info("观测路径和模式路径{}, {}", modelDataPath, obsDataPath);
        filterModelPath = "\""  + SplitYearUtils.getFilterYear(Integer.valueOf(startYear), Integer.valueOf(endYear),
                modelDataPath + varName) + "\"";
        filterObsPath = "\""  + SplitYearUtils.getFilterYear(Integer.valueOf(startYear), Integer.valueOf(endYear),
                obsDataPath + varName) + "\"";

        String shellPath = null;
        String dataoutPath = null;
        String logPath = null;
        //区分是偏差还是不是偏差
        int count = 0;
        if(varNameIndex % 2 == 0){
            //说明是正常的，不是偏差
            shellPath = HsPathUtils.Hs_Normal_Shell_Path + meanTypeArray[meanTypeIndex] + "/";
            dataoutPath = HsPathUtils.Hs_Normal_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
            logPath = HsPathUtils.Hs_Normal_Shell_Path + meanTypeArray[meanTypeIndex] + "/log/" + "log_" +  varName +
                    "_" + startYear + "_" + caseNum + "_" + monthAndDay + "_" + hour + "_" + obsPathName;
        }else{
            count = count + 1;
            shellPath = HsPathUtils.Hs_Normal_dev_Shell_Path + meanTypeArray[meanTypeIndex] + "/";
            dataoutPath = HsPathUtils.Hs_Normal_dev_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
            logPath =
                    HsPathUtils.Hs_Normal_dev_Shell_Path + meanTypeArray[meanTypeIndex] + "/log/" + "log_" +  varName +
                    "_" + startYear + "_" + caseNum + "_" + monthAndDay + "_" + hour + "_" + obsPathName;


        }
        // 拼接数据 提交至服务器执行
        StringBuffer sb = new StringBuffer();
        sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                .append(obsDataName + " ").append(modelDataPath + " ").append(obsDataPath + " ").append(varName + " ")
                .append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
        sb.append("> " + logPath);
        System.out.println(sb);
        Result result = ParseShellUtils.parse(sb.toString());
        // 图片生成结束后，要验证是否生成成功
        //存放未生成图片的信息
        List<Map<String, Object>> picResultList = new ArrayList<>();
        Integer start_year = Integer.valueOf(startYear);
        Integer end_year = Integer.valueOf(endYear);
        //图片添加字段的index count为图片后缀index
        if("0".equals(meanType)){
            count = count;
        }else if("1".equals(meanType)){
            count = count + 2;
        }else if("2".equals(meanType)){
            count = count + 4;
        }
        List<String> pathList = new ArrayList<>();
        pathList.add(varName);
        pathList.add(caseNum);
        pathList.add(monthAndDay + hour);
        pathList.add(pictureNameArray[count]);
        pathList.add(obsDataName);
        for(int i = start_year; i<=end_year; i++){
            if(i > start_year){
                // 先删除之前的年份  换成新的年份插入
                pathList.remove(1);
            }
            pathList.add(1, String.valueOf(i));
            String pictureName = dataoutPath + pathName(pathList);
            log.info("pictureName{}", pictureName);
            File file = new File(pictureName);
            if(!file.exists()){
                Map<String, Object> picMap = new HashMap<>();
                picMap.put("year", i);
                picMap.put("varName", varName);
                picMap.put("caseNum", caseNum);
                picMap.put("mmddhour", monthAndDay + hour);
                picMap.put("dataName", obsDataName);
                picResultList.add(picMap);
                System.out.println("no============");
            }else {
                resultList.add(pictureName);
            }
        }
        //说明存在年份没有生成对应图片
        if(picResultList.size() > 0){
            resultList.add(picResultList.toString());
        }
        lock.unlock();
        return resultList;
    }

    @Override
    public List<Map<String, Object>> batchExecWeatherFirstPicture(Map<String, Object> map) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //类型  气温-0/降水-1/10米风-2/2米比湿-3
        String typeName = map.get("typeName") + "";
        //类别  延伸期-0/月尺度-1/季节尺度-2
        String meanType = map.get("meanType") + "";
        //起报month+day
        String monthAndDay = map.get("mmDd") + "";
        //起报时次 hour
        List<String> hourList = (List<String>) map.get("hour");
        //case
        List<String> caseNumList = (List<String>) map.get("caseNum");
        //观测资料 cfsr/eri/cn_obs站点
        String obsDataName = map.get("obsDataName") + "";
        //月尺度对应月份
        String month = map.get("month") + "";
        //季尺度季度
        String seas = map.get("seas") + "";
        String varName = map.get("varName") + "";
        String startYear = map.get("startYear") + "";
        String endYear = map.get("endYear") + "";
        for(String hour : hourList){
            for(String caseNum : caseNumList){
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("typeName", typeName);
                paramMap.put("meanType", meanType);
                paramMap.put("mmDd", monthAndDay);
                paramMap.put("hour", hour);
                paramMap.put("caseNum", caseNum);
                paramMap.put("obsDataName", obsDataName);
                paramMap.put("month", month);
                paramMap.put("seas", seas);
                paramMap.put("varName", varName);
                paramMap.put("startYear", startYear);
                paramMap.put("endYear", endYear);
                List<String> list = execWeatherFirstPicture(paramMap);
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("hour", hour);
                resultMap.put("caseNum", caseNum);
                resultMap.put("result", list);
                resultList.add(resultMap);
            }
        }

        return resultList;
    }


    /**
     * 气温出图 第二张提交绘图
     * @param map
     * @return
     *
     *
     * {
     *     "typeName": 0,
     *     "meanType": 2,
     *     "mmDd": "0302",
     *     "hour": "00",
     *     "caseNum": "01",
     *     "obsDataName": "cn_obs",
     *     "month": "03",
     *     "seas": "0",
     *     "varName": "0",
     *     "startYear": "1991",
     *     "endYear": "1992"
     * }
     */
    @Override
    public String weatherSecondPicture(Map<String, Object> map) {

        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        List<String> resultList = new ArrayList<>();
        //类型  气温-0/降水-1/10米风-2/2米比湿-3
        String typeName = map.get("typeName") + "";
        //类别  延伸期-0/月尺度-1/季节尺度-2
        String meanType = map.get("meanType") + "";
        //起报month+day
        String monthAndDay = map.get("mmDd") + "";
        //起报时次 hour
        String hour = map.get("hour") + "";
        //case
        String caseNum = map.get("caseNum") + "";
        //观测资料 cfsr/eri/cn_obs站点
        String obsDataName = map.get("obsDataName") + "";
        //要素 分3种情况 需要根据类别 延伸期-0/月尺度-1/季节尺度-2区分
        //气温：平均气温-0/平均气温偏差-1/平均日最高气温-2/平均日最高气温偏差-3/平均日最低气温-4/平均日最低气温偏差-5
        //降水：平均降水-0/平均降水偏差-1
        //10米风： 纬向风速-0/经向风速-1/平均10米风速-2/纬向风速偏差-3/经向风速偏差-4/平均10米风速偏差-5/
        //2米比湿： 2米比湿-0/2米比湿偏差-1
        //平均气温对应的是AT2M，日最高气温对应的是T2MAX。日最低气温对应的是T2MIN，降水就是PRAVG，风速就是WSAVG
        String varName = map.get("varName") + "";
        String startYear = map.get("startYear") + "";
        String endYear = map.get("endYear") + "";
        //月尺度  出图月份
        String month = map.get("month") + "";
        //季节尺度  出图季度
        String seas = map.get("seas") + "";
        String obsDataPath = null;
        String modelDataPath = null;
        String filterModelPath = null;
        String filterObsPath = null;
        //用于记录obsDataPath内路径名称
        String obsPathName = null;
        //obsDataName名称转换
        if ("CN05".equalsIgnoreCase(obsDataName)){
            obsDataName = "CN05";
            obsPathName = "CN_OBS";
        }else if("CFSR".equalsIgnoreCase(obsDataName)){
            obsDataName = "";
            obsPathName = "";
        }else if("ERI".equalsIgnoreCase(obsDataName)){
            obsDataName = "ERI";
            obsPathName = "ERI";
        }
        //具体路径拼接  需要区分  1.气温 降水 10米风 2米比湿    2.延伸期 月尺度 季节尺度
        int varNameIndex = Integer.valueOf(varName);
        if ("0".equals(typeName)){
            //气温
            varName = weatherVarName[varNameIndex];
        }else if ("1".equals(typeName)){
            //降水
            varName = pravgVarName[varNameIndex];
        }else if ("2".equals(typeName)){
            //10米风
            varName = wsavgVarName[varNameIndex];
        }else if ("3".equals(typeName)){
            //2米比湿
            varName = otherVarName[varNameIndex];
        }

        obsDataPath = obs_base_path + obsPathName + "/";
        modelDataPath = model_base_path + monthAndDay + "/POST_C" + caseNum + "_GCMSST/" + monthAndDay + hour + "/";
        // 根据起报时次mmdd和hour区分出obsDataPath  modelDataPath
        if ("0302".equals(monthAndDay)){
            if("00".equals(hour)){
                obsDataPath = obsDataPath + "0302/";
            }else {
                obsDataPath = obsDataPath + "0303/";
            }
        }else if("0401".equals(monthAndDay)){
            if("00".equals(hour)){
                obsDataPath = obsDataPath + "0401/";
            }else {
                obsDataPath = obsDataPath + "0402/";
            }
        }
        int meanTypeIndex = Integer.valueOf(meanType);
        modelDataPath = modelDataPath + shellMeanTypeArray[meanTypeIndex] + "/";
        obsDataPath = obsDataPath + shellMeanTypeArray[meanTypeIndex] + "/";
        log.info("观测路径和模式路径{}, {}", modelDataPath, obsDataPath);
        filterModelPath = "\""  + SplitYearUtils.getFilterYear(Integer.valueOf(startYear), Integer.valueOf(endYear),
                modelDataPath + varName) + "\"";
        filterObsPath = "\""  + SplitYearUtils.getFilterYear(Integer.valueOf(startYear), Integer.valueOf(endYear),
                obsDataPath + varName) + "\"";

        String shellPath = null;
        String dataoutPath = null;
        String logPath = null;
        //区分是偏差还是不是偏差
        int count = 0;
        if(varNameIndex % 2 == 0){
            //说明是正常的，不是偏差
            shellPath = HsPathUtils.Hs_Normal_Climate_Shell_Path + meanTypeArray[meanTypeIndex] + "/";
            dataoutPath = HsPathUtils.Hs_Normal_Climate_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
            logPath =
                    HsPathUtils.Hs_Normal_Climate_Shell_Path + meanTypeArray[meanTypeIndex] + "/log/" + "log_" +  varName +
                    "_" + startYear + "_" + endYear + "_" + caseNum + "_" + monthAndDay + "_" + hour + "_" + obsPathName;
        }else{
            count = count + 1;
            shellPath = HsPathUtils.Hs_Normal_Climate_dev_Shell_Path + meanTypeArray[meanTypeIndex] + "/";
            dataoutPath = HsPathUtils.Hs_Normal_Climate_dev_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
            logPath =
                    HsPathUtils.Hs_Normal_Climate_dev_Shell_Path + meanTypeArray[meanTypeIndex] + "/log/" + "log_" +  varName +
                    "_" + startYear + "_" + endYear + "_" + caseNum + "_" + monthAndDay + "_" + hour + "_" + obsPathName;


        }
        // 拼接数据 提交至服务器执行
        StringBuffer sb = new StringBuffer();
        String monthName = null;
        String seasName = null;
        //提交参数 区分是延伸期  月尺度  季节尺度
        if("0".equals(meanType)){
            sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                    .append(obsDataName + " ").append(modelDataPath + " ").append(obsDataPath + " ").append(varName + " ")
                    .append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
            sb.append("> " + logPath);
        }else if("1".equals(meanType)){
            //首先减3  从3月开始  如果是0401 则再减去1
            int monthIndex = Integer.valueOf(month) - 3;
            monthName = monthEngName[monthIndex];
            if("0401".equals(monthAndDay)){
                monthIndex = monthIndex - 1;
            }
            sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                    .append(obsDataName + " ").append(monthName + " ").append(monthIndex + " ").append(modelDataPath +
                    " ").append(obsDataPath + " ").append(varName + " ").append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
            sb.append("> " + logPath);
        }else if("2".equals(meanType)){
            int seasIndex = Integer.valueOf(seas);
            seasName = seasEngName[seasIndex];
            //0302时次  0代表春季  1代表夏季  0401时次  1代表夏季  没有春季
            if("0401".equals(monthAndDay)){
                seasIndex = seasIndex - 1;
            }
            sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                    .append(obsDataName + " ").append(seasName + " ").append(seasIndex + " ").append(modelDataPath +
                    " ").append(obsDataPath + " ").append(varName + " ").append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
            sb.append("> " + logPath);
        }
        System.out.println(sb);
        Result result = ParseShellUtils.parse(sb.toString());
        // 图片生成结束后，要验证是否生成成功
        //存放未生成图片的信息
        List<Map<String, Object>> picResultList = new ArrayList<>();
        //图片添加字段的index count为图片后缀index
        if("0".equals(meanType)){
            count = count;
        }else if("1".equals(meanType)){
            count = count + 2;
        }else if("2".equals(meanType)){
            count = count + 4;
        }
        List<String> pathList = new ArrayList<>();
        pathList.add(varName);
        pathList.add(caseNum);
        if("0".equals(meanType)){
            pathList.add(monthAndDay + hour);
        }else if("1".equals(meanType)){
            pathList.add(monthAndDay + hour + monthName);
        }else if("2".equals(meanType)){
            pathList.add(monthAndDay + hour + seasName);
        }
        pathList.add(startYear);
        pathList.add(endYear);
        pathList.add(pictureMoreYearNameArray[count]);
        pathList.add(obsDataName);
        String pictureName = dataoutPath + pathName(pathList);
        log.info("pictureName{}", pictureName);
        File file = new File(pictureName);
        if(!file.exists()){
            picResultList.add(map);
            System.out.println("no============");
        }
        //说明存在年份没有生成对应图片
        if(picResultList.size() > 0){
            pictureName = picResultList.toString();
        }
        lock.unlock();
        return pictureName;
    }

    /**
     * 气温出图 第三张提交绘图
     * @param map
     * @return
     *
     * {
     *     "typeName": 0,
     *     "meanType": 0,
     *     "mmDd": "0302",
     *     "hour":  "00",
     *     "caseNum":  "01",
     *     "obsDataName": "CN05",
     *     "minLat":"15",
     *     "maxLat":"55",
     *     "minLon":"70",
     *     "maxLon":"140",
     *     "month": "",
     *     "seas": "0",
     *     "varName": "0",
     *     "startYear": "1991",
     *     "endYear": "1992",
     *     "year":1992
     * }
     *
     */
    @Override
    public Result weatherThirdPicture(Map<String, Object> map) {

        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        //类型  气温-0/降水-1/10米风-2/2米比湿-3
        String typeName = map.get("typeName") + "";
        //类别  延伸期-0/月尺度-1/季节尺度-2
        String meanType = map.get("meanType") + "";
        //起报month+day
        String monthAndDay = map.get("mmDd") + "";
        //起报时次 hour
        String hour = map.get("hour") + "";
        //case
        String caseNum = map.get("caseNum") + "";
        //观测资料 cfsr/eri/cn_obs站点
        String obsDataName = map.get("obsDataName") + "";
        //要素 分3种情况 需要根据类别 延伸期-0/月尺度-1/季节尺度-2区分
        //气温：平均气温-0/平均气温偏差-1/平均日最高气温-2/平均日最高气温偏差-3/平均日最低气温-4/平均日最低气温偏差-5
        //降水：平均降水-0/平均降水偏差-1
        //10米风： 纬向风速-0/经向风速-1/平均10米风速-2/纬向风速偏差-3/经向风速偏差-4/平均10米风速偏差-5/
        //2米比湿： 2米比湿-0/2米比湿偏差-1
        //平均气温对应的是AT2M，日最高气温对应的是T2MAX。日最低气温对应的是T2MIN，降水就是PRAVG，风速就是WSAVG
        String varName = map.get("varName") + "";
        String startYear = map.get("startYear") + "";
        String endYear = map.get("endYear") + "";
        String year = map.get("year") + "";
        //月尺度  出图月份
        String month = map.get("month") + "";
        //季节尺度  出图季度
        String seas = map.get("seas") + "";
        //位置信息
        String minLat = map.get("minLat") + "";
        String maxLat = map.get("maxLat") + "";
        String minLon = map.get("minLon") + "";
        String maxLon = map.get("maxLon") + "";
        String obsDataPath = null;
        String modelDataPath = null;
        String filterModelPath = null;
        String filterObsPath = null;
        //用于记录obsDataPath内路径名称
        String obsPathName = null;
        //obsDataName名称转换
        if ("CN05".equalsIgnoreCase(obsDataName)){
            obsDataName = "CN05";
            obsPathName = "CN_OBS";
        }else if("CFSR".equalsIgnoreCase(obsDataName)){
            obsDataName = "";
            obsPathName = "";
        }else if("ERI".equalsIgnoreCase(obsDataName)){
            obsDataName = "ERI";
            obsPathName = "ERI";
        }
        //具体路径拼接  需要区分  1.气温 降水 10米风 2米比湿    2.延伸期 月尺度 季节尺度
        int varNameIndex = Integer.valueOf(varName);
        if ("0".equals(typeName)){
            //气温
            varName = weatherVarName[varNameIndex];
        }else if ("1".equals(typeName)){
            //降水
            varName = pravgVarName[varNameIndex];
        }else if ("2".equals(typeName)){
            //10米风
            varName = wsavgVarName[varNameIndex];
        }else if ("3".equals(typeName)){
            //2米比湿
            varName = otherVarName[varNameIndex];
        }

        obsDataPath = obs_base_path + obsPathName + "/";
        modelDataPath = model_base_path + monthAndDay + "/POST_C" + caseNum + "_GCMSST/" + monthAndDay + hour + "/";
        // 根据起报时次mmdd和hour区分出obsDataPath  modelDataPath
        if ("0302".equals(monthAndDay)){
            if("00".equals(hour)){
                obsDataPath = obsDataPath + "0302/";
            }else {
                obsDataPath = obsDataPath + "0303/";
            }
        }else if("0401".equals(monthAndDay)){
            if("00".equals(hour)){
                obsDataPath = obsDataPath + "0401/";
            }else {
                obsDataPath = obsDataPath + "0402/";
            }
        }
        int meanTypeIndex = Integer.valueOf(meanType);
        modelDataPath = modelDataPath + shellMeanTypeArray[meanTypeIndex] + "/";
        obsDataPath = obsDataPath + shellMeanTypeArray[meanTypeIndex] + "/";
        log.info("观测路径和模式路径{}, {}", modelDataPath, obsDataPath);
        filterModelPath = "\""  + SplitYearUtils.getFilterYear(Integer.valueOf(startYear), Integer.valueOf(endYear),
                modelDataPath + varName) + "\"";
        filterObsPath = "\""  + SplitYearUtils.getFilterYear(Integer.valueOf(startYear), Integer.valueOf(endYear),
                obsDataPath + varName) + "\"";

        String shellPath = null;
        String dataoutPath = null;
        String logPath = null;
        //区分是区域平均折线图还是格点图
        if(varNameIndex % 2 == 0){
            int count = 0;
            List<String> title1List = new ArrayList<>();
            List<String> title2List = new ArrayList<>();
            int meanTypeTitleIndex = Integer.valueOf(meanType);
            //图片左上角信息
            if("1".equals(typeName)){
                //降水
                title1List.add(titleAnomalyNameArray[meanTypeTitleIndex + 3]);
            }else {
                title1List.add(titleAnomalyNameArray[meanTypeTitleIndex]);
            }
            title1List.add(varName);
            //图片右上角信息
            if("0".equals(meanType)){
                title2List.add(monthAndDay + hour);
            }else if("1".equals(meanType)){
                int monthIndex = Integer.valueOf(month) - 3;
                title2List.add(monthAndDay + hour + " " + monthEngName[monthIndex]);
            }else if("2".equals(meanType)){
                int seasIndex = Integer.valueOf(seas);
                title2List.add(monthAndDay + hour + " " + seasEngName[seasIndex]);
            }
            title2List.add("case" + caseNum);
            //说明是区域平均折线图  需要现选现画
            shellPath = HsPathUtils.Hs_Normal_Anomaly_Txt_Shell_Path + meanTypeArray[meanTypeIndex] + "/";
            dataoutPath = HsPathUtils.Hs_Normal_Anomaly_Txt_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
            logPath =
                    HsPathUtils.Hs_Normal_Anomaly_Txt_Shell_Path + meanTypeArray[meanTypeIndex] + "/log/" + "log_" +  varName +
                    "_" + startYear + "_" + endYear + "_" + caseNum + "_" + monthAndDay + "_" + hour + "_" + obsPathName;

            // 拼接数据 提交至服务器执行
            StringBuffer sb = new StringBuffer();
            String monthName = null;
            String seasName = null;
            //提交参数 区分是延伸期  月尺度  季节尺度
            if("0".equals(meanType)){
                sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                        .append(obsDataName + " ").append(minLat + " ").append(maxLat + " ").append(minLon + " ").append(maxLon + " ")
                        .append(modelDataPath + " ").append(obsDataPath + " ").append(varName + " ")
                        .append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
                sb.append("> " + logPath);
            }else if("1".equals(meanType)){
                //首先减3  从3月开始  如果是0401 则再减去1
                int monthIndex = Integer.valueOf(month) - 3;
                monthName = monthEngName[monthIndex];
                if("0401".equals(monthAndDay)){
                    monthIndex = monthIndex - 1;
                }
                sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                        .append(obsDataName + " ").append(monthName + " ").append(monthIndex + " ").append(minLat +
                        " ").append(maxLat + " ").append(minLon + " ").append(maxLon + " ").append(modelDataPath +
                        " ").append(obsDataPath + " ").append(varName + " ").append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
                sb.append("> " + logPath);
            }else if("2".equals(meanType)){
                int seasIndex = Integer.valueOf(seas);
                seasName = seasEngName[seasIndex];
                //0302时次  0代表春季  1代表夏季  0401时次  1代表夏季  没有春季
                if("0401".equals(monthAndDay)){
                    seasIndex = seasIndex - 1;
                }
                sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                        .append(obsDataName + " ").append(seasName + " ").append(seasIndex + " ").append(minLat +
                        " ").append(maxLat + " ").append(minLon + " ").append(maxLon + " ").append(modelDataPath +
                        " ").append(obsDataPath + " ").append(varName + " ").append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
                sb.append("> " + logPath);
            }
            System.out.println(sb);
            Result result = ParseShellUtils.parse(sb.toString());
            // txt文件生成结束后，要验证是否生成成功
            //存放未生成图片的信息
            List<Map<String, Object>> picResultList = new ArrayList<>();
            //txt文件添加字段的index count为图片后缀index
            if("0".equals(meanType)){
                count = count;
            }else if("1".equals(meanType)){
                count = count + 2;
            }else if("2".equals(meanType)){
                count = count + 4;
            }
            List<String> pathList = new ArrayList<>();
            pathList.add(varName);
            pathList.add(startYear);
            pathList.add(endYear);
            pathList.add(caseNum);
            if("0".equals(meanType)){
                pathList.add(monthAndDay + hour);
                pathList.add(pictureAnomalyNameArray[count]);
            }else if("1".equals(meanType)){
                pathList.add(monthAndDay + hour);
                pathList.add(pictureAnomalyNameArray[count]);
                pathList.add(monthName);
            }else if("2".equals(meanType)){
                pathList.add(monthAndDay + hour);
                pathList.add(pictureAnomalyNameArray[count]);
                pathList.add(seasName);
            }
            pathList.add(obsDataName);
            String txtName = dataoutPath + txtPathName(pathList);
            log.info("txtName{}", txtName);
            File file = new File(txtName);
            if(!file.exists()){
                //说明存在年份没有生成对应txt文件
                picResultList.add(map);
                System.out.println("no============");
                return Result.error("图片生成异常，请重试!");
            }
            //存储返回结果
            Map<String, Object> resultTxt = new HashMap<>();
            try {
                Map<String, Object> resultMap = ReadServiceTxtUtils.readTxt(file);
                List<Integer> yearList = new ArrayList<>();
                int s = Integer.valueOf(startYear);
                int e = Integer.valueOf(endYear);
                for(int i = s; i <= e; i++){
                    yearList.add(i);
                }
                //返回接口拼装
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("year", yearList);
                dataMap.put("title1", title1List);
                dataMap.put("title2", title2List);
                dataMap.put("yAxis", "anomaly");
                dataMap.put("data",resultMap);
                resultTxt.put("data", dataMap);

            } catch (IOException e) {
                e.printStackTrace();
            }
            lock.unlock();
            return Result.ok(resultTxt);
        }else{
            int count = 1;
            //格点分布图  直接取图片路径
            List<String> pathList = new ArrayList<>();
            pathList.add(varName);
            pathList.add(year);
            if("0".equals(meanType)){
                count = count;
                pathList.add(caseNum);
                pathList.add(hour);
                pathList.add(pictureAnomalyNameArray[count]);
            }else if("1".equals(meanType)){
                int monthIndex = Integer.valueOf(month) - 3;
                count = count + 2;
                pathList.add(year);
                pathList.add(caseNum);
                pathList.add(hour);
                pathList.add(pictureAnomalyNameArray[count]);
                pathList.add(monthEngName[monthIndex]);
            }else if("2".equals(meanType)){
                count = count + 4;
                int seasIndex = Integer.valueOf(seas);
                pathList.add(year);
                pathList.add(caseNum);
                pathList.add(hour);
                pathList.add(pictureAnomalyNameArray[count]);
                pathList.add(seasEngName[seasIndex]);
            }
            pathList.add(obsDataName);
            String pictureName = HsPathUtils.Hs_Normal_Anomaly_Picture_Path + pathName(pathList);
            return Result.ok(pictureName);
        }
    }


    /**
     * 生成的图片名称拼接
     * @param pathList
     * @return
     */
    private String pathName(List<String> pathList){
        StringBuffer sb = new StringBuffer();
        for (String i: pathList) {
            sb.append("_").append(i);
        }
        return sb.toString().replaceFirst("_", "") + ".png";
    }

    /**
     * 生成的txt名称拼接
     * @param pathList
     * @return
     */
    private String txtPathName(List<String> pathList){
        StringBuffer sb = new StringBuffer();
        for (String i: pathList) {
            sb.append("_").append(i);
        }
        return sb.toString().replaceFirst("_", "") + ".txt";
    }

    public static void main(String[] args) {
        File file = new File("F:\\AT2M_1991_1992_01_030200_ysqmean_anomaly_CN05.txt");
        try {
            Map<String, Object> resultMap = ReadServiceTxtUtils.readTxt(file);
            System.out.println(resultMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
