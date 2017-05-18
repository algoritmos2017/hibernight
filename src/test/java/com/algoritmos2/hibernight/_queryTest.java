package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.repository.Query;
import com.algoritmos2.hibernight.model.Direccion;
import com.algoritmos2.hibernight.model.Person;
import com.algoritmos2.hibernight.model.annotations.Table;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;

public class _queryTest {
	String xql = "$nombre=? and $direccion.calle=?";
	
	@Test
	public void _queryIntegral(){
		//assertTrue(true);
		Query._query(Person.class, xql);
		
		/*System.out.println(Arrays.asList(Person.class.getDeclaredAnnotations()).stream()
        .filter(annotation -> annotation.annotationType().getSimpleName().equals("Table"))
        .findFirst()
        .map(a -> ((Table) a).name()));*/
	}
}
