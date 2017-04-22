package com.algoritmos2.hibernight.config;

import org.hibernate.SessionFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

import javax.naming.ConfigurationException;
import javax.sql.DataSource;
import java.util.Properties;

public class DataBaseConfig {

    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) throws ConfigurationException {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory());
        return transactionManager;
    }

    public SessionFactory sessionFactory() throws ConfigurationException {
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(localDataSource());

        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.show_sql", true);

        sessionBuilder.addProperties(properties);
        sessionBuilder.scanPackages("com.algoritmos2.hibernight");

        return sessionBuilder.buildSessionFactory();
    }

    public DataSource localDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/hibernight");
        dataSource.setUsername("root");
        dataSource.setPassword("");
        return dataSource;
    }
}