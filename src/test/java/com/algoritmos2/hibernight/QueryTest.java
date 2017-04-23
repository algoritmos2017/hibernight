package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.config.DataBaseConfig;
import com.algoritmos2.hibernight.model.Person;
import com.algoritmos2.hibernight.model.mapper.Mapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.ConfigurationException;
import java.sql.Connection;
import java.sql.SQLException;

public class QueryTest extends DataBaseConfig {

    Connection connection;

    @Before
    public void setUp() throws SQLException {
        connection = getConnection();
    }

    @After
    public void finalize() throws SQLException {
        closeConnection();
    }

    @Test
    public void list() throws ConfigurationException, SQLException {
        //System.out.println(Person.class.getAnnotations()[0].annotationType().getTypeName());

        Mapper mapper = new Mapper();

        mapper.createTable(Person.class);

        //String xql = "SELECT * FROM clazz where person_name = ?";
        /*
        Statement stmt = connection.createStatement();

        String sql = "CREATE TABLE Person " +
                "(id INTEGER not NULL, " +
                " person_name VARCHAR(255), " +
                " last VARCHAR(255), " +
                " age INTEGER, " +
                " PRIMARY KEY ( id ))";

        stmt.executeUpdate(sql);
        */
    }

}
