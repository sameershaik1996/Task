package com.redshift.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableHystrix
@EnableCaching
public class RedshiftPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedshiftPaymentApplication.class, args);
	}

}
