package com.tulip.springboot_mybatis_mysql_part.config;


import com.github.pagehelper.PageHelper;
import com.tulip.springboot_mybatis_mysql_part.enums.DataSourceType;
import com.tulip.springboot_mybatis_mysql_part.utils.DataSourceContextHolder;
import com.tulip.springboot_mybatis_mysql_part.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
@AutoConfigureAfter(DataSourceConfig.class)
@MapperScan(basePackages = "com.tulip.springboot_mybatis_mysql_part.dao")
public class MybatisConfig {

    @Value("${mysql.datasource.readSize}")
    private String readDataSourceSize;

    @Value("${mysql.datasource.mapperLocations}")
    private String mapperLocation;

    @Value("${mysql.datasource.configLocation}")
    private String configLocation;

    @Resource
    @Qualifier("writeDataSource")
    private DataSource writeDataSource;
    @Resource
    @Qualifier("readDataSource01")
    private DataSource readDataSource01;
    @Resource
    @Qualifier("readDataSource02")
    private DataSource readDataSource02;

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        log.info("******************sqlSessionFactory init***********************");

        try {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

            sqlSessionFactoryBean.setDataSource(roundRobinDataSouceProxy());

            sqlSessionFactoryBean.setTypeAliasesPackage("com.tulip.springboot_mybatis_mysql_part.domain");

            sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocation));

            sqlSessionFactoryBean.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));

            Interceptor[] plugins = new Interceptor[]{pageHelper()};

            sqlSessionFactoryBean.setPlugins(plugins);
            return sqlSessionFactoryBean.getObject();


        } catch (Exception e) {
            log.error("mybatis sqlSessionFactoryBean create error",e);
        }
        return  null;

    }

    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        p.setProperty("returnPageInfo", "check");
        p.setProperty("params", "count=countSql");
        pageHelper.setProperties(p);
        return pageHelper;
    }

    @Bean(name="roundRobinDataSouceProxy")
    public AbstractRoutingDataSource roundRobinDataSouceProxy() {

        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();

        targetDataSources.put(DataSourceType.write.getType(), writeDataSource);
        targetDataSources.put(DataSourceType.read.getType()+"1", readDataSource01);
        targetDataSources.put(DataSourceType.read.getType()+"2", readDataSource02);

        final int readSize = Integer.parseInt(readDataSourceSize);

        AbstractRoutingDataSource proxy = new AbstractRoutingDataSource(){
            private AtomicInteger count = new AtomicInteger(0);

            @Override
            protected Object determineCurrentLookupKey() {
                String typeKey = DataSourceContextHolder.getReadOrWrite();

                if(typeKey == null){

                    throw new NullPointerException("数据库路由时，决定使用哪个数据库源类型不能为空...");
                }

                if (typeKey.equals(DataSourceType.write.getType())){
                    System.err.println("使用数据库write.............");
                    return DataSourceType.write.getType();
                }

                //读库， 简单负载均衡
                int number = count.getAndAdd(1);
                System.err.println("number-"+(number));
                int lookupKey = number % readSize;
                System.err.println("使用数据库read-"+(lookupKey+1));
                return DataSourceType.read.getType()+(lookupKey+1);
            }
        };

        proxy.setDefaultTargetDataSource(writeDataSource);//默认库
        proxy.setTargetDataSources(targetDataSources);
        return proxy;
    }


    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    //事务管理
    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager((DataSource) SpringContextUtil.getBean("roundRobinDataSouceProxy"));
    }
}
