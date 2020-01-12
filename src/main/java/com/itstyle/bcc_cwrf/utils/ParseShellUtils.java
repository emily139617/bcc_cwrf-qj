package com.itstyle.bcc_cwrf.utils;

import com.itstyle.bcc_cwrf.common.entity.Result;
import com.itstyle.bcc_cwrf.web.HbNormalWeatherController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

/**
 * @Description: 提交服务器画图执行脚本
 * @Author hy
 * @Date 2019-12-26 10:05
 * @Version 1.0
 */

public class ParseShellUtils {
    private final static Logger log = LoggerFactory.getLogger(HbNormalWeatherController.class);

    public static Result parse(String shellName){

        BufferedReader br = null;
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        StringBuilder sb = null;
        try {
            sb = new StringBuilder();
            log.info("Starting to exec:{}. PID is:{} ", shellName, pid);
            ProcessBuilder processBuilder = getProcessBuilder(shellName);
            Process process = null;
            process = processBuilder.start();
            getShellExecString(sb, process);
        } catch (Exception e) {
            log.error("Error occured when exec cmd", e);
        } finally {
            closeFlush(br);
        }

        return Result.ok();
    }

    public static ProcessBuilder getProcessBuilder(String shellName) {
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", shellName);
        pb.environment();
        pb.redirectErrorStream(true);
        return pb;
    }

    public static void getShellExecString(StringBuilder stringBuilder, Process process) throws IOException,
            InterruptedException {
        BufferedReader br;
        String s = "";
        if (process != null) {
            br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()), 1024);
            while ((s = br.readLine()) != null) {
                stringBuilder.append(s);
                stringBuilder.append("\n");
                log.info(s);
            }
            log.info("wait for:{}", process.waitFor());
        } else {
            log.info("There is no PID found.");
        }
    }

    /**
     *  关闭流
     * @param closeables
     */
    public static void closeFlush(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
