package com.tulip.springboot_mybatis_mysql_part;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAutoConfiguration
@EnableTransactionManagement(order = 10) //开启事务，并设置order值，默认是Integer的最大值
@ComponentScan(basePackages={"com.tulip.springboot_mybatis_mysql_part"})
@SpringBootApplication
public class SpringbootMybatisMysqlPartApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMybatisMysqlPartApplication.class, args);
	}
}
