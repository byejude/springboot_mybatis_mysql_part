package com.tulip.springboot_mybatis_mysql_part.utils;

import com.tulip.springboot_mybatis_mysql_part.enums.DataSourceType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSourceContextHolder {

    private static ThreadLocal<String> local = new ThreadLocal<>();

    private static  ThreadLocal<String> getLocal(){
        return local;
    }

    public static void setRead(){
        local.set(DataSourceType.read.getType());
    }

    public static  void setWrite(){
        local.set(DataSourceType.write.getType());
    }

    public static String getReadOrWrite(){
        return local.get();
    }

    public static void clear(){
        local.remove();
    }

}
