package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.config.DataBaseConfig;
import com.algoritmos2.hibernight.model.Direccion;
import com.algoritmos2.hibernight.model.Persona;
import com.algoritmos2.hibernight.model.mapper.Mapper;
import com.algoritmos2.hibernight.repository.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.ConfigurationException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Mapper mapper = new Mapper();

        Statement stmt = connection.createStatement();
        System.out.println(mapper.createTableQuery(Direccion.class));
        //stmt.executeUpdate(mapper.createTableQuery(Direccion.class));
    }

    
    @Test
    public void query1() throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException {
        String xql = "";

       List <Direccion> lista = Query.query(connection, Direccion.class, xql);
       for(Direccion p : lista ){
    	   System.out.println(p.getPersonas());
       }
        
        
    }
    

    @Test
    public void patternToGetFields() throws SQLException {
        List<String> fields = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\$(.*?)=\\?");
        String xql = "$nombre=? and $direccion.calle=? and $ocupacion=?";
        Matcher matcher = pattern.matcher(xql);

        Arrays.asList("pepi", "av rivada", "asdads");

        while (matcher.find()) {
            fields.add(matcher.group(1));
        }

        System.out.println(fields);
        //Query.query(connection, Person.class, xql, Arrays.asList("jorg","dreccion", "trabajo"));
    }

    @Test
    public void query() throws SQLException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String xql = "$nombre=? and $direccion.numero=?";
        //List<Persona> personas = Query.query(connection,Persona.class, xql, "Diego", 111);

    }
}