package com.itstyle.bcc_cwrf.utils;

import com.itstyle.bcc_cwrf.web.HbNormalWeatherController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: TODO
 * @Author hy
 * @Date 2019-12-25 10:26
 * @Version 1.0
 */

public class SplitYearUtils {
    private final static Logger log = LoggerFactory.getLogger(HbNormalWeatherController.class);

    public static String getFilterYear(int startYear, int endYear, String preFix){
        log.info("输入的年份 start{}, end{}", startYear, endYear);
        String tmp = null;
        if(startYear > 1990 && startYear < 2000){
            int startYearIndex = startYear - 1990;
            //[1990-1999]
            if(endYear < 2000) {
                int yearIndex = endYear - 1990;
                tmp = "ls " + preFix + "_199[" + startYearIndex + "-" + yearIndex + "]*";
            }else if(endYear >= 2000 && endYear < 2010){
                //[1991-2009]
                int yearIndex = endYear - 2000;
                tmp = "ls " + preFix + "_199["+ startYearIndex + "-9]* -a " + preFix + "_200[0-" + yearIndex + "]*";
            }else if(endYear >= 2010 && endYear < 2020){
                //[1991-2019]
                int yearIndex = endYear - 2010;
                tmp = "ls " + preFix + "_199[" +startYearIndex + "-9]* -a " + preFix + "_200[0-9]* -a " + preFix +
                        "_201[0-" + yearIndex+ "]*";
            }else if(endYear >= 2020 && endYear < 2030){
                //[2020-2030]
                int yearIndex = endYear - 2020;
                tmp = "ls " + preFix + "_199[" +startYearIndex + "-9]* -a " + preFix + "_200[0-9]* -a " + preFix +
                        "_201[0-9]* -a " + preFix + "_202[0-" + yearIndex+ "]*";
            }

        }else if(startYear >= 2000 && startYear < 2010){
            int startYearIndex = startYear - 2000;
            //[2000-2010]
            if(endYear < 2010) {
                int yearIndex = endYear - 2000;
                tmp = "ls " + preFix + "_200[" + startYearIndex + "-" + yearIndex + "]*";
            }else if(endYear >= 2010 && endYear < 2020){
                //[2000-2019]
                int yearIndex = endYear - 2010;
                tmp = "ls " + preFix + "_200[" + startYearIndex + "-9]* -a " + preFix + "_201[0-" + yearIndex + "]*";

            }else if(endYear >= 2020 && endYear < 2030){
                //[2020-2030]
                int yearIndex = endYear - 2020;
                tmp = "ls " + preFix + "_200[" + startYearIndex + "-9]* -a " + preFix + "_201[0-9]* -a " + preFix +
                        "_202[-"+ yearIndex + "]*";
            }
        }else if(startYear >= 2010 && startYear < 2020){
            int startYearIndex = startYear - 2010;
            if(endYear < 2020){
                int yearIndex = endYear - 2010;
                //[2010-2019]
                tmp = "ls " + preFix + "_201[" + startYearIndex + "-" + yearIndex + "]*";
            }else if(endYear >= 2020 && endYear < 2030){
                //[2010-2029]
                int yearIndex = endYear - 2020;
                tmp = "ls " + preFix + "_201[" + startYearIndex + "-9]* -a " + preFix + "_202[0-" + yearIndex + "]*";
            }
        } else if(startYear >= 2020 && startYear < 2030){
            int startYearIndex = startYear - 2020;
            int yearIndex = endYear - 2020;
            //[2020-2029]
            tmp = "ls " + preFix + "_202[" + startYearIndex + "-" + yearIndex + "]*";
        }
        log.info("输出结果{}", tmp);
        return tmp;
    }

}
