package org.evote.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import javax.sql.DataSource;

@Configuration
public class DatabaseInitializationConfig {

    @Value("classpath:init_users.sql")
    private Resource initDataScript1;

    @Value("classpath:init_votes.sql")
    private Resource initDataScript2;

    @Autowired
    private Environment env;

    @Profile("!test")
    @Bean
    public DataSource usersDataSourceInit() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.users.driverClassName"));
        dataSource.setUrl(env.getProperty("spring.datasource.users.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.users.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.users.password"));
        return dataSource;
    }

    @Profile("!test")
    @Bean
    public DataSource votesDataSourceInit() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.votes.driverClassName"));
        dataSource.setUrl(env.getProperty("spring.datasource.votes.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.votes.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.votes.password"));

        return dataSource;
    }

    @Profile("test")
    @Bean
    public DataSource testUsersDataSourceInit() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.test.users.driverClassName"));
        dataSource.setUrl(env.getProperty("spring.datasource.test.users.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.test.users.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.test.users.password"));
        return dataSource;
    }

    @Profile("test")
    @Bean
    public DataSource testVotesDataSourceInit() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.test.votes.driverClassName"));
        dataSource.setUrl(env.getProperty("spring.datasource.test.votes.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.test.votes.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.test.votes.password"));
        return dataSource;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer1() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        if (!env.acceptsProfiles("test")) {
            databasePopulator.addScript(initDataScript1);
        }

        DataSourceInitializer initializer = new DataSourceInitializer();
        if (env.acceptsProfiles("test")) {
            initializer.setDataSource(testUsersDataSourceInit());
        } else {
            initializer.setDataSource(usersDataSourceInit());
        }
        initializer.setDatabasePopulator(databasePopulator);

        return initializer;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer2() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        if (!env.acceptsProfiles("test")) {
            databasePopulator.addScript(initDataScript2);
        }

        DataSourceInitializer initializer = new DataSourceInitializer();
        if (env.acceptsProfiles("test")) {
            initializer.setDataSource(testVotesDataSourceInit());
        } else {
            initializer.setDataSource(votesDataSourceInit());
        }
        initializer.setDatabasePopulator(databasePopulator);

        return initializer;
    }
}