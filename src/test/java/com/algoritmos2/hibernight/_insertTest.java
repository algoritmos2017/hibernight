package com.algoritmos2.hibernight;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.algoritmos2.hibernight.model.Direccion;
import com.algoritmos2.hibernight.model.Ocupacion;
import com.algoritmos2.hibernight.model.Persona;
import com.algoritmos2.hibernight.model.mapper.Mapper;

public class _insertTest {
	
	private Persona personaPrueba = new Persona();
	private Direccion dir = new Direccion();
	private Ocupacion ocu = new Ocupacion();
	private String insertEsperado = "INSERT INTO persona (id_direccion,id_ocupacion,nombre,id_persona) VALUES (2,30,'Pancurulo',1);";
	
	@Before
	public void setUp(){
		dir.setIdDireccion(2);
		ocu.setIdOcupacion(30);
		
		personaPrueba.setDireccion(dir);
		personaPrueba.setIdPersona(1);
		personaPrueba.setNombre("Pancurulo");
		personaPrueba.setOcupacion(ocu);
	}
	
	@Test
	public void test_insert(){
		assertEquals(insertEsperado, Mapper._insert(personaPrueba));
	}
}
