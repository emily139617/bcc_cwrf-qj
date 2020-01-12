package com.itstyle.bcc_cwrf.service.Impl;
/**
 * @Description: TODO
 * @Author hy
 * @Date 2020-01-10 16:58
 * @Version 1.0
 */

import com.itstyle.bcc_cwrf.common.entity.Result;
import com.itstyle.bcc_cwrf.service.HbEvaluationService;
import com.itstyle.bcc_cwrf.utils.HbPathUtils;
import com.itstyle.bcc_cwrf.utils.ParseShellUtils;
import com.itstyle.bcc_cwrf.utils.ReadServiceTxtUtils;
import com.itstyle.bcc_cwrf.utils.SplitYearUtils;
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

@Service
public class HbEvaluationServiceImpl implements HbEvaluationService {

    private final static Logger log = LoggerFactory.getLogger(HbNormalWeatherController.class);

    /**
     * 常规气候模块要素
     */
    // 月份数字转英文
    private static final String[] monthEngName = {"Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept"};
    // 季度数字转英文
    private static final String[] seasEngName = {"MAM", "JJA"};
    // 空间相关系数txt文件后缀
    private static final String[] sccTxtName = {"ysqmean_scc", "monmean_scc", "seasmean_scc"};
    // 延伸期，月尺度，季尺度数组
    private static final String[] meanTypeArray = {"ysqmean", "monmean", "seasmean"};
    private static final String[] shellMeanTypeArray = {"ysqmean", "monmean", "seasonmean"};
    // 时间相关系数txt文件后缀
    private static final String[] tccTxtName = {"ysqmean_tcc", "monmean_tcc", "seasmean_tcc"};


    /**
     * obsDataPath\ modelDataPath 路径  使用时需要添加相关信息 区分延伸期，月尺度，季尺度
     */
    private static final String obs_base_path = "/GFPS8p/sw_BData/bcccsmProduct/obs/hb/";
    private static final String model_base_path = "/GFPS8p/sw_BData/bcccsmProduct/model/hb/";
    /**
     * 执行空间相关系数脚本  出txt文件
     * @param map
     * @return
        {
        "meanType": 0,
        "mmDd": "0302",
        "hour": "00",
        "caseNum": "01",
        "obsDataName": "CN05",
        "month": "03",
        "seas": "0",
        "varName": "AT2M",
        "startYear": "1991",
        "endYear": "1992"
        }
     */
    @Override
    public Result execSccTxt(Map<String, Object> map) {

        ReentrantLock lock = new ReentrantLock();
        lock.lock();
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
        String obsDataPath = obs_base_path + obsPathName + "/";
        String modelDataPath = model_base_path + monthAndDay + "/POST_C" + caseNum + "_GCMSST/" + monthAndDay + hour + "/";;
        String filterModelPath = null;
        String filterObsPath = null;
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
        String shellPath = HbPathUtils.Hb_Evaluation_SCC_Shell_Path + meanTypeArray[meanTypeIndex] + "/";
        String dataoutPath = HbPathUtils.Hb_Evaluation_SCC_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
        String logPath = HbPathUtils.Hb_Evaluation_SCC_Shell_Path + meanTypeArray[meanTypeIndex] + "/log/" + "log_" +  varName +
                "_" + startYear + "_" + caseNum + "_" + monthAndDay + "_" + hour + "_" + obsPathName;
        StringBuffer sb = new StringBuffer();
        if("0".equals(meanType)){
            sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                    .append(obsDataName + " ").append(modelDataPath + " ").append(obsDataPath + " ").append(varName + " ")
                    .append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
            sb.append("> " + logPath);
            System.out.println(sb);
            Result result = ParseShellUtils.parse(sb.toString());
        }else if("1".equals(meanType)){
            //首先减3  从3月开始  如果是0401 则再减去1
            int monthIndex = Integer.valueOf(month) - 3;
            String monthName = monthEngName[monthIndex];
            if("0401".equals(monthAndDay)){
                monthIndex = monthIndex - 1;
            }
            sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                    .append(obsDataName + " ").append(monthName + " ").append(monthIndex + " ").append(modelDataPath +
                    " ").append(obsDataPath + " ").append(varName + " ").append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
            sb.append("> " + logPath);
            System.out.println(sb);
            Result result = ParseShellUtils.parse(sb.toString());
        }else if("2".equals(meanType)){
            int seasIndex = Integer.valueOf(seas);
            String seasName = seasEngName[seasIndex];
            //0302时次  0代表春季  1代表夏季  0401时次  1代表夏季  没有春季
            if("0401".equals(monthAndDay)){
                seasIndex = seasIndex - 1;
            }
            sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                    .append(obsDataName + " ").append(seasName + " ").append(seasIndex + " ").append(modelDataPath +
                    " ").append(obsDataPath + " ").append(varName + " ").append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
            sb.append("> " + logPath);
            System.out.println(sb);
            Result result = ParseShellUtils.parse(sb.toString());
        }
        // txt文件生成结束后，要验证是否生成成功
        //存放未生成图片的信息
        List<Map<String, Object>> picResultList = new ArrayList<>();
        //记录txt生成路径
        List<String> pathList = new ArrayList<>();
        pathList.add(varName);
        pathList.add(startYear);
        pathList.add(endYear);
        pathList.add(caseNum);
        pathList.add(monthAndDay + hour);
        pathList.add(sccTxtName[meanTypeIndex]);
        if("1".equals(meanType)){
            int monthIndex = Integer.valueOf(month) - 3;
            pathList.add(monthEngName[monthIndex]);
        }else if("2".equals(meanType)){
            int seasIndex = Integer.valueOf(seas);
            pathList.add(seasEngName[seasIndex]);
        }
        pathList.add(obsDataName);
        String txtName = dataoutPath + txtPathName(pathList);
        log.info("txtName{}", txtName);
        File file = new File(txtName);
        if(!file.exists()){
            //说明存在年份没有生成对应txt文件
            picResultList.add(map);
            System.out.println("no============");
            return Result.error("文件生成异常，请重试!");
        }
        List<String> title1List = new ArrayList<>();
        List<String> title2List = new ArrayList<>();
        if("0".equals(meanType)){
            title1List.add("Extended forecast Average Spatial Correlation Coefficient");
            title2List.add(monthAndDay + hour);
        }else if("1".equals(meanType)){
            title1List.add("Monthly Average Spatial Correlation Coefficient");
            int monthIndex = Integer.valueOf(month) - 3;
            title2List.add(monthAndDay + hour + " " + monthEngName[monthIndex]);
        }else if("2".equals(meanType)){
            title1List.add("Seasonal Average Spatial Correlation Coefficient");
            int seasIndex = Integer.valueOf(seas);
            title2List.add(monthAndDay + hour + " " + seasEngName[seasIndex]);
        }
        title1List.add(varName);
        title2List.add("case" + caseNum);
        //存储返回结果
        Map<String, Object> resultTxt = new HashMap<>();
        try {
            Map<String, Object> resultMap = ReadServiceTxtUtils.readSccTxt(file);
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
    }

    /**
     * 直接给出图片路径
     * @param map
     * @return
     * {
     *     "meanType": 2,
     *     "mmDd": "0302",
     *     "hour": "00",
     *     "caseNum": "01",
     *     "obsDataName": "CN05",
     *     "month": "05",
     *     "seas": "0",
     *     "varName": "AT2M",
     *     "startYear": "1991",
     *     "endYear": "1994"
     * }
     *
     */
    @Override
    public Result execTccPicture(Map<String, Object> map) {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
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
        //obsDataName名称转换
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
        String obsDataPath = obs_base_path + obsPathName + "/";
        String modelDataPath = model_base_path + monthAndDay + "/POST_C" + caseNum + "_GCMSST/" + monthAndDay + hour + "/";;
        String filterModelPath = null;
        String filterObsPath = null;
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
        String shellPath = HbPathUtils.Hb_Evaluation_TCC_Shell_Path + meanTypeArray[meanTypeIndex] + "/";
        String dataoutPath = HbPathUtils.Hb_Evaluation_TCC_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
        String logPath =
                HbPathUtils.Hb_Evaluation_TCC_Shell_Path + meanTypeArray[meanTypeIndex] + "/log/" + "log_" +  varName +
                "_" + startYear + "_" + caseNum + "_" + monthAndDay + "_" + hour + "_" + obsPathName;
        StringBuffer sb = new StringBuffer();
        if("0".equals(meanType)){
            sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                    .append(obsDataName + " ").append(modelDataPath + " ").append(obsDataPath + " ").append(varName + " ")
                    .append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
            sb.append("> " + logPath);
            System.out.println(sb);
            Result result = ParseShellUtils.parse(sb.toString());
        }else if("1".equals(meanType)){
            //首先减3  从3月开始  如果是0401 则再减去1
            int monthIndex = Integer.valueOf(month) - 3;
            String monthName = monthEngName[monthIndex];
            if("0401".equals(monthAndDay)){
                monthIndex = monthIndex - 1;
            }
            sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                    .append(obsDataName + " ").append(monthName + " ").append(monthIndex + " ").append(modelDataPath +
                    " ").append(obsDataPath + " ").append(varName + " ").append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
            sb.append("> " + logPath);
            System.out.println(sb);
            Result result = ParseShellUtils.parse(sb.toString());
        }else if("2".equals(meanType)){
            int seasIndex = Integer.valueOf(seas);
            String seasName = seasEngName[seasIndex];
            //0302时次  0代表春季  1代表夏季  0401时次  1代表夏季  没有春季
            if("0401".equals(monthAndDay)){
                seasIndex = seasIndex - 1;
            }
            sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                    .append(obsDataName + " ").append(seasName + " ").append(seasIndex + " ").append(modelDataPath +
                    " ").append(obsDataPath + " ").append(varName + " ").append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
            sb.append("> " + logPath);
            System.out.println(sb);
            Result result = ParseShellUtils.parse(sb.toString());
        }
        // txt文件生成结束后，要验证是否生成成功
        int meanTyepIndex = Integer.valueOf(meanType);
        //记录picture生成路径
        List<String> pathList = new ArrayList<>();
        pathList.add(varName);
        pathList.add(startYear);
        pathList.add(endYear);
        pathList.add(caseNum);
        if("0".equals(meanType)){
            pathList.add(monthAndDay + hour);
        }else if("1".equals(meanType)){
            int monthIndex = Integer.valueOf(month) - 3;
            pathList.add(monthAndDay + hour + monthEngName[monthIndex]);
        }else if("2".equals(meanType)){
            int seasIndex = Integer.valueOf(seas);
            pathList.add(monthAndDay + hour + seasEngName[seasIndex]);
        }
        pathList.add(tccTxtName[meanTyepIndex]);
        pathList.add(obsDataName);
        String pictureName = dataoutPath + pathName(pathList);
        log.info("pictureName{}", pictureName);
        File file = new File(pictureName);
        if(!file.exists()){
            return Result.error("图片生成异常，请重试!");
        }
        return Result.ok(pictureName);
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

}
