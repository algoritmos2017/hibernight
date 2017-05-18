package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.repository.Query;
import com.algoritmos2.hibernight.model.Direccion;
import com.algoritmos2.hibernight.model.Persona;
import com.algoritmos2.hibernight.model.annotations.Table;
import com.algoritmos2.hibernight.model.mapper.Mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class _queryTest {
	String xql = "$nombre=? and $direccion.calle=?";
	
	@Test
	public void _queryIntegral(){
		//assertTrue(true);
		Query._query(Persona.class, xql, "Juan", 2);
		
	}
}
