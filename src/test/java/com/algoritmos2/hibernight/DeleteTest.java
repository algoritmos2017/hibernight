package com.algoritmos2.hibernight;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.algoritmos2.hibernight.config.DataBaseConfig;
import com.algoritmos2.hibernight.model.*;
import com.algoritmos2.hibernight.model.mapper.Mapper;
import com.algoritmos2.hibernight.repository.Query;

public class DeleteTest extends DataBaseConfig
{
	String xql = "";
	String sqlEsperado= "DELETE persona FROM persona INNER JOIN direccion ON persona.id_direccion=direccion.id_direccion  WHERE nombre=? AND direccion.calle=? ";
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
	public void _delete()
	{
		xql = "$nombre=? and $direccion.calle=?";
		String sql = Query._delete(Persona.class, xql);
		//System.out.println(sql);	
		Assert.assertEquals(sqlEsperado,sql);
		
	}
	
	@Test
	 public void delete() throws SQLException {
	        
		xql = "$nombre=? and $direccion.calle=?";
			int rows = Query.delete(connection,Persona.class, xql, "Pablo", "Mozart");
		
			//System.out.println(rows);
			Assert.assertEquals(0,rows);
			

	 }
	
}	 
