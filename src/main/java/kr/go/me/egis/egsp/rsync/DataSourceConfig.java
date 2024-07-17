package kr.go.me.egis.egsp.rsync;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    private final ApplicationContext applicationContext;

    public DataSourceConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    /** DataSource Internal 생성 */
    @Bean
    @Primary
    @Qualifier("intDataSource")
    @ConfigurationProperties(prefix = "spring.datasource-int")
    public DataSource intDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    /** sqlSessionFactory Internal 생성 */
    @Bean
    @Primary
    @Qualifier("intSqlSessionFactory")
    public SqlSessionFactory intSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(intDataSource());
        factoryBean.setVfs(SpringBootVFS.class);
        factoryBean.setConfigLocation(applicationContext.getResource("classpath:/mapper/mybatis-config.xml"));
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resource = resolver.getResources("classpath:/mapper/int/*.xml");
        factoryBean.setMapperLocations(resource);
        factoryBean.setTypeAliasesPackage("kr.go.me.egis.egsp.rsync.biz");
        return factoryBean.getObject();
    }

    @Bean
    @Primary
    @Qualifier("intSqlSessionTemplate")
    public SqlSessionTemplate intSqlSessionTemplate(@Autowired @Qualifier("intSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }


    @Bean
    @Qualifier("extDataSource")
    @ConfigurationProperties(prefix = "spring.datasource-ext")
    public DataSource extDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @Qualifier("extSqlSessionFactory")
    public SqlSessionFactory extSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(extDataSource());
        factoryBean.setVfs(SpringBootVFS.class);
        factoryBean.setConfigLocation(applicationContext.getResource("classpath:/mapper/mybatis-config.xml"));
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resource = resolver.getResources("classpath:/mapper/ext/*.xml");
        factoryBean.setMapperLocations(resource);
        factoryBean.setTypeAliasesPackage("kr.go.me.egis.egsp.rsync.biz");
        return factoryBean.getObject();
    }

    @Bean
    @Qualifier("extSqlSessionTemplate")
    public SqlSessionTemplate extSqlSessionTemplate(@Autowired @Qualifier("extSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
