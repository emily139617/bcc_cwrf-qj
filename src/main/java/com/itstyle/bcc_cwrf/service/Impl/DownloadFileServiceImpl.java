package com.itstyle.bcc_cwrf.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itstyle.bcc_cwrf.service.DownloadFileService;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: zhengyy
 * @Date: 2020/1/10 14:39
 * @Version 1.0
 */
@Service
public class DownloadFileServiceImpl implements DownloadFileService {

    /**
     *
     * @param map
     *@param response
     * json参数
     *
     *{
     * "mmDd": "0302",
     * "hour": "00",
     * "caseName":"01",
     * "startYear": "1991",
     * "endYear": "2019",
     * "variables": ["PRAVG","AQ2M","AT2M"],
     * "typeName": "daily"   extended延伸期 monmean月平均 seasonmean季平均 annual年平均
     * }
     */
    @Override
    public void downloadHBFile(Map<String, Object> map, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject(map);
        switch (jsonObject.getString("typeName")) {
            case "daily": {
                String path = "/home/export/base/YVDI/sw_BData/GPFS8p/zip/hb_daily.tar.bz2";
                String name = "hb_daily.tar.bz2";
                download(path, name, response);
                break;
            }
            case "monmean": {
                String path = "/home/export/base/YVDI/sw_BData/GPFS8p/zip/hb_mon.tar.bz2";
                String name = "hb_mon.tar.bz2";
                download(path, name, response);
                break;
            }
            case "extended": {
                String path = "/home/export/base/YVDI/sw_BData/GPFS8p/zip/hb_extended.tar.bz2";
                String name = "hb_extended.tar.bz2";
                download(path, name, response);
                break;
            }
            default: {
                String path = "/home/export/base/YVDI/sw_BData/GPFS8p/zip/hb_seasonmean.tar.bz2";
                String name = "hb_seasonmean.tar.bz2";
                download(path, name, response);
                break;
            }
        }
//        List<String> list = getHBList(jsonObject);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        String date = sdf.format(new Date());
//        String name = date.concat(".bz2");
//        String path = "/home/export/base/YVDI/sw_BData/GPFS8p/zip/".concat(name);
//        boolean flag = compress(list, path);
//        if (flag) {
//            download(path, name, response);
//        }
    }

    /**
     *
     * @param map
     *@param response
     * json参数
     *
     *{
     * "caseName":"01",
     * "startYear": "1991",
     * "endYear": "2019",
     * "variables": ["PRAVG","AQ2M","AT2M"],
     * "typeName": "daily"   extended延伸期 monmean月平均 seasonmean季平均 annual年平均
     * }
     */
    @Override
    public void downloadHSFile(Map<String, Object> map, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject(map);
        switch (jsonObject.getString("typeName")) {
            case "daily": {
                String path = "/home/export/base/YVDI/sw_BData/GPFS8p/zip/hs_daily.tar.bz2";
                String name = "hs_daily.tar.bz2";
                download(path, name, response);
                break;
            }
            case "monmean": {
                String path = "/home/export/base/YVDI/sw_BData/GPFS8p/zip/hs_mon.tar.bz2";
                String name = "hs_mon.tar.bz2";
                download(path, name, response);
                break;
            }
            case "seasonmean": {
                String path = "/home/export/base/YVDI/sw_BData/GPFS8p/zip/hs_seasonmean.tar.bz2";
                String name = "hs_seasonmean.tar.bz2";
                download(path, name, response);
                break;
            }
            default: {
                String path = "/home/export/base/YVDI/sw_BData/GPFS8p/zip/hs_yearmean.tar.bz2";
                String name = "hs_yearmean.tar.bz2";
                download(path, name, response);
                break;
            }
        }
//        List<String> list = getHSList(jsonObject);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        String date = sdf.format(new Date());
//        String name = date.concat(".bz2");
//        String path = "/home/export/base/YVDI/sw_BData/GPFS8p/zip/".concat(name);
//        boolean flag = compress(list, path);
//        if (flag) {
//            download(path, name, response);
//        }
    }

    private List<String> getHBList(JSONObject jsonObject) {
        String time = jsonObject.getString("mmDd").concat(jsonObject.getString("hour"));
        String case_name = "POST_C".concat(jsonObject.getString("caseName")).concat("_GCMSST");
        String report_time = jsonObject.getString("mmDd");
        String typeName = jsonObject.getString("typeName");
        //压缩的回报数据目录
        String hb_path = "/home/export/base/YVDI/sw_BData/GPFS8p/bcccsmProduct/compressed_model/hb";
        File file = new File(hb_path);
        List<String> list = new ArrayList<>();
        File[] files = file.listFiles();
        assert files != null;
        for (File file2 : files) {
            if (file2.isDirectory() && file2.getName().equals(report_time)) {
                File[] files1 = file2.listFiles();
                assert files1 != null;
                for (File file1 : files1) {
                    if (file1.isDirectory() && file1.getName().equals(case_name)) {
                        File[] files2 = file1.listFiles();
                        assert files2 != null;
                        for (File file3: files2) {
                            if (file3.isDirectory() && file3.getName().equals(time)) {
                                File[] files3 = file3.listFiles();
                                assert files3 != null;
                                for (File file4: files3) {
                                    switch (typeName) {
                                       case "daily":
                                            if (file4.isDirectory() && file4.getName().equals("daily")) {
                                                File[] dailyFiles = file4.listFiles();
                                                List<String> daily_list = getPathByCon(jsonObject.getJSONArray("variables"), jsonObject.getString("startYear"), jsonObject.getString("endYear"), dailyFiles);
                                                list.addAll(daily_list);
                                            }
                                            break;
                                        case "extended":
                                            if (file4.isDirectory() && file4.getName().equals("ysqmean")) {
                                                File[] dailyFiles = file4.listFiles();
                                                List<String> daily_list = getPathByCon(jsonObject.getJSONArray("variables"), jsonObject.getString("startYear"), jsonObject.getString("endYear"), dailyFiles);
                                                list.addAll(daily_list);
                                            }
                                            break;
                                        case "monmean":
                                            if (file4.isDirectory() && file4.getName().equals("monmean")) {
                                                File[] dailyFiles = file4.listFiles();
                                                List<String> daily_list = getPathByCon(jsonObject.getJSONArray("variables"), jsonObject.getString("startYear"), jsonObject.getString("endYear"), dailyFiles);
                                                list.addAll(daily_list);
                                            }
                                            break;
                                        default:
                                            if (file4.isDirectory() && file4.getName().equals("seasonmean")) {
                                                File[] dailyFiles = file4.listFiles();
                                                List<String> daily_list = getPathByCon(jsonObject.getJSONArray("variables"), jsonObject.getString("startYear"), jsonObject.getString("endYear"), dailyFiles);
                                                list.addAll(daily_list);
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    private List<String> getHSList(JSONObject jsonObject) {
        String case_name = "case".concat(jsonObject.getString("caseName")).concat("_GCMSST");
        String typeName = jsonObject.getString("typeName");
        String hs_path = "/home/export/base/YVDI/sw_BData/GPFS8p/bcccsmProduct/compressed_model/hs";
        File file = new File(hs_path);
        List<String> list = new ArrayList<>();
        File[] files = file.listFiles();
        assert files != null;
        for (File file2 : files) {
            if (file2.isDirectory() && file2.getName().equals(case_name)) {
                File[] files1 = file2.listFiles();
                assert files1 != null;
                for (File file1 : files1) {
                    switch (typeName) {
                        case "daily":
                            if (file1.isDirectory() && file1.getName().equals("daily")) {
                                File[] dailyFiles = file1.listFiles();
                                List<String> daily_list = getPathByCon(jsonObject.getJSONArray("variables"), jsonObject.getString("startYear"), jsonObject.getString("endYear"), dailyFiles);
                                list.addAll(daily_list);
                            }
                            break;
                        case "extended":
                            if (file1.isDirectory() && file1.getName().equals("monmean")) {
                                File[] dailyFiles = file1.listFiles();
                                List<String> daily_list = getPathByCon(jsonObject.getJSONArray("variables"), jsonObject.getString("startYear"), jsonObject.getString("endYear"), dailyFiles);
                                list.addAll(daily_list);
                            }
                            break;
                        case "monmean":
                            if (file1.isDirectory() && file1.getName().equals("seasonmean")) {
                                File[] dailyFiles = file1.listFiles();
                                List<String> daily_list = getPathByCon(jsonObject.getJSONArray("variables"), jsonObject.getString("startYear"), jsonObject.getString("endYear"), dailyFiles);
                                list.addAll(daily_list);
                            }
                            break;
                        default:
                            if (file1.isDirectory() && file1.getName().equals("yearmean")) {
                                File[] dailyFiles = file1.listFiles();
                                List<String> daily_list = getPathByCon(jsonObject.getJSONArray("variables"), jsonObject.getString("startYear"), jsonObject.getString("endYear"), dailyFiles);
                                list.addAll(daily_list);
                            }
                            break;

                    }
                }
            }
        }
        return list;
    }

    private List<String> getPathByCon(JSONArray jsonArray, String startYear, String endYear, File[] files) {
        List<String> list = new ArrayList<>();
        int start = Integer.parseInt(startYear);
        int end = Integer.parseInt(endYear);
        for (Object var : jsonArray) {
            for (int i = start; i < end + 1; i++) {
                String pat = var.toString().concat(".*.").concat(String.valueOf(i));
                Pattern p = Pattern.compile(pat);
                for (File file: files) {
                    Matcher m = p.matcher(file.getName());
                    if (m.find()) {
                        list.add(file.getAbsolutePath());
                        break;
                    }
                }
            }
        }
        return list;
    }

    private boolean compress(List<String> filePaths, String zipFilePath) {
        File zipFile = new File(zipFilePath);
        if(!zipFile.exists()) {
            try {
                boolean flag = zipFile.createNewFile();
                if (flag) {
                    System.out.println("创建成功！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        boolean f;
        BZip2CompressorOutputStream bzip2OS;
        try {
            bzip2OS = new BZip2CompressorOutputStream(new FileOutputStream(zipFile), 8);
            for (String relativePath : filePaths) {
                if (StringUtils.isEmpty(relativePath)) {
                    continue;
                }
                File sourceFile = new File(relativePath);
                if (!sourceFile.exists()) {
                    continue;
                }
                FileInputStream fis = new FileInputStream(sourceFile);
                int count;
                byte[] buf = new byte[8];
                while ((count = fis.read(buf, 0, buf.length)) != -1) {
                    bzip2OS.write(buf, 0, count);
                }
                bzip2OS.finish();
                bzip2OS.flush();
            }
            f = true;
            bzip2OS.close();
        } catch (Exception e) {
            f = false;
            e.printStackTrace();
        }
        return f;
    }

    private void download(String path, String name, HttpServletResponse response) {
        File file = new File(path);
        if (file.exists()) {
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + name);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
