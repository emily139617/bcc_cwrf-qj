package com.itstyle.bcc_cwrf.utils;

import org.codehaus.groovy.tools.shell.util.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 读取服务器上txt文件工具类
 * @Author hy
 * @Date 2019-12-26 10:19
 * @Version 1.0
 */

public class ReadServiceTxtUtils {
    /**
     * 回报指数
     * @param file
     * @return
     * @throws IOException
     */
    //获取txt里面的结果，用List返回
    public static Map<String, Object> readTxt(File file) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> obsData = new ArrayList<>();
        List<String> modelData = new ArrayList<>();
        String s = null;
        InputStreamReader in = new InputStreamReader(new FileInputStream(file),"UTF-8");
        BufferedReader br = new BufferedReader(in);
        List<String> stringList= new ArrayList<>();
        while ((s=br.readLine())!=null){
            stringList.add(s.replaceAll("\"","").replaceAll(" +",";"));
        }
        for(int i=1; i<stringList.size(); i++){
            String data = stringList.get(i);
            data = data.replaceFirst(";", "");
            String[] dataArray = data.split(";");
            obsData.add(dataArray[0]);
            modelData.add(dataArray[1]);
        }
        resultMap.put("obs", obsData);
        resultMap.put("model", modelData);
        return resultMap;
    }
    //获取夏季风(6-8)指数txt里面的结果，每一行一年，用List返回
    public static Map<String, Object> readSummerWindTxt(File file) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> obsData = new ArrayList<>();
        List<String> modelData = new ArrayList<>();
        InputStreamReader in = new InputStreamReader(new FileInputStream(file),"UTF-8");
        BufferedReader br = new BufferedReader(in);
        while (br.readLine() != null){
            String s = br.readLine();
            String[] data = s.split(" ");
            obsData.add(data[1]);
            modelData.add(data[0]);
        }
        resultMap.put("obs", obsData);
        resultMap.put("model", modelData);
        return resultMap;
    }

    /**
     * 空间相关系数读取txt
     * @param file
     * @return
     * @throws IOException
     */
    //获取txt里面的结果，用List返回
    public static Map<String, Object> readSccTxt(File file) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> obsData = new ArrayList<>();
        List<String> modelData = new ArrayList<>();
        String s = null;
        InputStreamReader in = new InputStreamReader(new FileInputStream(file),"UTF-8");
        BufferedReader br = new BufferedReader(in);
        List<String> stringList= new ArrayList<>();
        while ((s=br.readLine())!=null){
            stringList.add(s.replaceAll("\"","").replaceAll(" +",";"));
        }
        for(int i=1; i<stringList.size(); i++){
            String data = stringList.get(i);
            data = data.replaceFirst(";", "");
            String[] dataArray = data.split(";");
            obsData.add(dataArray[0]);
        }
        resultMap.put("data", obsData);
        return resultMap;
    }

}
