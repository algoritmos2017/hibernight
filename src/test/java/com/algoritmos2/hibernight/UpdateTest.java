package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.config.DataBaseConfig;
import com.algoritmos2.hibernight.model.mapper.Mapper;
import com.algoritmos2.hibernight.model.profeModel.Direccion;
import com.algoritmos2.hibernight.model.profeModel.Persona;
import com.algoritmos2.hibernight.repository.Query;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class UpdateTest extends DataBaseConfig {

    private Connection connection = getConnection();

    @Test
    public void _update() {
        String query = Query._update(Persona.class, "set $nombre=?, $ ocupacion=? where $nombre=?");

       assertEquals("UPDATE persona SET nombre=?,id_ocupacion=? WHERE nombre=?", query);
    }

    @Test
    public void update() throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException, SecurityException, NoSuchFieldException {
        // query = Query.update(connection, Direccion.class, "set $numero=? where $numero=?", "11", "2331");

    	Persona persona = Query.find(connection,Persona.class,10);
    	System.out.println("nombre Original: "+persona.getNombre());
    	//persona.setNombre("Analia2");
    	System.out.println("Nombre Cambiado: "+persona.getNombre());
    	//Query.update(connection,Persona.class,"set $nombre=? where $idPersona=?","Analia",10);
    	Query.update(connection,persona);
    	persona = Query.find(connection,Persona.class,10);
    	System.out.println("nombre Original segunda vuelta: "+persona.getNombre());
    	

    	
    }

}