package com.tulip.springboot_mybatis_mysql_part.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DataSourceConfig {

    @Value("${mysql.datasource.type}")
    private Class<? extends DataSource> dataSourceType;

    @Bean(name = "writeDataSource")
    @Primary
    @ConfigurationProperties(prefix = "mysql.datasource.write")
    public DataSource writeDataSource(){
        log.info("*******************writeDataSource init***********************");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

    @Bean(name = "readDataSource01")
    @Primary
    @ConfigurationProperties(prefix = "mysql.datasource.read01")
    public DataSource readDataSource01(){
        log.info("*******************readDataSource01 init***********************");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

    @Bean(name = "readDataSource02")
    @Primary
    @ConfigurationProperties(prefix = "mysql.datasource.read02")
    public DataSource readDataSource02(){
        log.info("*******************readDataSource02 init***********************");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }
}
