package com.tulip.springboot_mybatis_mysql_part.aspect;

import com.tulip.springboot_mybatis_mysql_part.enums.DataSourceType;
import com.tulip.springboot_mybatis_mysql_part.utils.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy(exposeProxy = true,proxyTargetClass = true)
public class DataSourceAopInService implements PriorityOrdered{

    @Pointcut("execution(* com.tulip.springboot_mybatis_mysql_part.service..*.*(..)) "
            + " && @annotation(com.tulip.springboot_mybatis_mysql_part.annotation.ReadDataSource) ")
    private void  readData(){
        log.info("********readData aop pointcut**********");
    }

    @Pointcut("execution(* com.tulip.springboot_mybatis_mysql_part.service..*.*(..)) "
            + " && @annotation(com.tulip.springboot_mybatis_mysql_part.annotation.WriteDataSource) ")
    private void  writeData(){
        log.info("********readData aop pointcut**********");
    }


    @Before(value = "readData()")
    public void readDataSourceType(){
        //如果写事务开启的话后续都是走写库
        log.info("****"+DataSourceContextHolder.getReadOrWrite());
        if(DataSourceContextHolder.getReadOrWrite()==null||!DataSourceContextHolder.getReadOrWrite().equals(DataSourceType.write)){
            DataSourceContextHolder.setRead();
        }
    }

    @Before(value = "writeData()")
    public void writeDataSourceType(){
        DataSourceContextHolder.setWrite();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
