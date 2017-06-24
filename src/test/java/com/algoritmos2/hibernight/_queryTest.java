package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.repository.Query;
import com.algoritmos2.hibernight.model.Direccion;
import com.algoritmos2.hibernight.model.Persona;
import com.algoritmos2.hibernight.model.annotations.Table;
import com.algoritmos2.hibernight.model.mapper.Mapper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class _queryTest {
	String xql = "";
	String sqlEsperado = "SELECT persona.id_persona, persona.nombre, persona.id_direccion, direccion.id_direccion, direccion.calle, direccion.numero, persona.id_ocupacion, ocupacion.id_ocupacion, ocupacion.descripcion, ocupacion.id_tipoocupacion, tipo_ocupacion.id_tipoocupacion, tipo_ocupacion.descripcion  FROM persona INNER JOIN direccion ON persona.id_direccion=direccion.id_direccion  INNER JOIN ocupacion ON persona.id_ocupacion=ocupacion.id_ocupacion  INNER JOIN tipo_ocupacion ON ocupacion.id_tipoocupacion=tipo_ocupacion.id_tipoocupacion  WHERE nombre=\"Juan\" AND direccion.calle=2 ";
	
	@Test
	public void _queryConArgumentos(){
		xql = "$nombre=? and $direccion.calle=?";
		String sqlDeLaFuncion = Query._query(Persona.class, xql, "Juan", 2);
		//System.out.println(Query._query(Persona.class,""));
		
		//System.out.println(Mapper.traducirDeObjetosARelacional(Persona.class));
		
		Assert.assertTrue(sqlDeLaFuncion.equals(sqlEsperado));
	}
	@Test
	public void _querySinArgumentos(){
		xql = "$nombre=Juan and $direccion.calle=2";
		String sqlDeLaFuncion = Query._query(Persona.class, xql);
		
		//System.out.println(sqlDeLaFuncion);
		Assert.assertTrue(sqlDeLaFuncion.equals(sqlEsperado));
	}
	@Test
	public void xqlConCaracteresInvalidosSeIgnoran(){
		xql = "$no(mbre=Jua)n an&d $direccion.call%e=2";
		String sqlDeLaFuncion = Query._query(Persona.class, xql);
		
		//System.out.println(sqlDeLaFuncion);
		Assert.assertTrue(sqlDeLaFuncion.equals(sqlEsperado));
	}
	@Test(expected = Error.class)
	public void _queryCantidadMenorDeArgumentosDaError(){
		xql = "$nombre=? and $direccion.calle=2";
		Query._query(Persona.class, xql);
	}
	@Test(expected = Error.class)
	public void _queryCantidadMayorDeArgumentosDaError(){
		xql = "$no(mbre=Jua)n an&d $direccion.call%e=2";
		Query._query(Persona.class, xql, "Juan", 2,3,5,6,7,2,"afs");
	}
}
