package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.config.DataBaseConfig;
import com.algoritmos2.hibernight.model.mapper.Mapper;
import com.algoritmos2.hibernight.model.profeModel.Persona;
import com.algoritmos2.hibernight.repository.Query;

import list;

import org.junit.After;
import org.junit.Assert;
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

 /*   @Test
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
*/
    @Test
    public void query() throws SQLException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
       
    	String xql = "$nombre = ?";
    	//System.out.println(xql.replaceFirst("?","");
    	//String xql = "";
      /*  List<Persona> personas = Query.query(connection,Persona.class, xql,"Analia");
        for(Persona p : personas){
        	System.out.println(p.getNombre());
        	System.out.println(p.getIdPersona());
        }
        
       Persona p = Query.find(connection,Persona.class,12);
        Assert.assertEquals(p.getNombre(),"Pablo");
        Assert.assertEquals((Integer)p.getOcupacion().getIdOcupacion(),(Integer)4);
*/
        List<Persona> p = Query.query(connection,Persona.class,"$idPersona = ?",12);
        System.out.println(p.get(0).getDirecciones());


    }
}