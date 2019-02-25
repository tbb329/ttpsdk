package com.zlst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *  入口
 */
@SpringBootApplication
@EnableScheduling
public class ZlstApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZlstApplication.class, args);
	}
}
