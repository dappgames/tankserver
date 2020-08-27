package io.tankgo.tankserver.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class NutDaoConfig {


    @Primary
    @Bean(name = "mysqlNutDao")
    public NutDao getNutDao() {
        DruidDataSource dataSource=new DruidDataSource();

        dataSource.setUrl("jdbc:mysql://rm-uf6bsdo45okbym4n0qo.mysql.rds.aliyuncs.com:3306/tankgodb?serverTimezone=GMT%2B8");
//        dataSource.setUrl("jdbc:mysql://rm-uf6bsdo45okbym4n0qo.mysql.rds.aliyuncs.com:3306/tankgodb?serverTimezone=UTC");
        dataSource.setUsername("tankadmin");
        dataSource.setPassword("snc2010$$");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setTestWhileIdle(true);

        dataSource.setValidationQuery("SELECT * FROM `tankgodb`.`tank_enter_log` ORDER BY `id` DESC LIMIT 1;");
        dataSource.setUseGlobalDataSourceStat(true);
        try {
            dataSource.setFilters("stat,wall,slf4j");
//            dataSource.setFilters("sl4j");
        }catch (Exception e){

        }
        NutDao nutDao = new NutDao(dataSource);
        return nutDao;
    }
}