package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.repository.Query;
import com.algoritmos2.hibernight.model.profeModel.*;
import com.algoritmos2.hibernight.model.QueryBuilder;
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
	String sqlEsperado = "SELECT persona.id_ocupacion, ocupacion.id_tipoocupacion, tipo_ocupacion.id_tipoocupacion, tipo_ocupacion.descripcion, ocupacion.id_ocupacion, ocupacion.descripcion, persona.id_persona, persona.nombre  FROM persona INNER JOIN ocupacion ON persona.id_ocupacion=ocupacion.id_ocupacion  INNER JOIN tipo_ocupacion ON ocupacion.id_tipoocupacion=tipo_ocupacion.id_tipoocupacion  WHERE persona.nombre='Juan' ";

	/*
	 * @Test public void _queryConArgumentos(){ xql =
	 * "$nombre=? and $direccion.calle=?"; String sqlDeLaFuncion =
	 * Query._query(Persona.class, xql, "Juan", 2);
	 * 
	 * System.out.println(sqlDeLaFuncion);
	 * 
	 * Assert.assertEquals(sqlDeLaFuncion, sqlEsperado); }
	 */
	
	@Test
	public void _querySinSignosDePregunta() {
		xql = "$nombre=Juan";
		String sqlDeLaFuncion = Query._query(Persona.class, xql);

		System.out.println(sqlDeLaFuncion);
		Assert.assertEquals(sqlDeLaFuncion, sqlEsperado);
	}
	
	@Test
	public void xqlConMuchosPuntos() {
		xql = "$ocupacion.tipoOcupacion.descripcion like ?";
		String sqlDeLaFuncion = Query._query(Persona.class, xql);
		sqlEsperado = "SELECT persona.id_ocupacion, ocupacion.id_tipoocupacion, tipo_ocupacion.id_tipoocupacion, tipo_ocupacion.descripcion, ocupacion.id_ocupacion, ocupacion.descripcion, persona.id_persona, persona.nombre  FROM persona INNER JOIN ocupacion ON persona.id_ocupacion=ocupacion.id_ocupacion  INNER JOIN tipo_ocupacion ON ocupacion.id_tipoocupacion=tipo_ocupacion.id_tipoocupacion  WHERE tipo_ocupacion.descripcion LIKE ? ";

		//System.out.println(sqlDeLaFuncion);
		Assert.assertEquals(sqlDeLaFuncion, sqlEsperado);
	}
	
	@Test
	public void where() {
		QueryBuilder queryBuilder = new QueryBuilder();
		String xql = "$ocupacion.tipoOcupacion.descripcion like ?";
		Mapper.obternerWhere(Persona.class, xql, queryBuilder);
		
		//System.out.println(queryBuilder.getWhere());
		Assert.assertEquals("tipo_ocupacion.descripcion LIKE ? ", queryBuilder.getWhere());
	}

	@Test(expected = Error.class)
	public void xqlConCaracteresInvalidosDaError() {
		xql = "$no(mbre=Jua)n an&d $direccion.call%e=2";
		Query._query(Persona.class, xql);
	}
	/*
	 * @Test(expected = Error.class) public void
	 * _queryCantidadMenorDeArgumentosDaError(){ xql =
	 * "$nombre=? and $direccion.calle=2"; Query._query(Persona.class, xql); }
	 * 
	 * @Test(expected = Error.class) public void
	 * _queryCantidadMayorDeArgumentosDaError(){ xql =
	 * "$no(mbre=Jua)n an&d $direccion.call%e=2"; Query._query(Persona.class,
	 * xql, "Juan", 2,3,5,6,7,2,"afs"); }
	 */
}
