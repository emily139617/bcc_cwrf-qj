package com.itstyle.bcc_cwrf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * 启动类
 * API接口测试：http://localhost:8080/seckill/swagger-ui.html
 */
@SpringBootApplication
public class Application {
	private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(Application.class, args);
		LOGGER.info("项目启动 ");
	}
}