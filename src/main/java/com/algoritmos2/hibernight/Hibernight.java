package com.algoritmos2.hibernight;


import java.util.List;

//$nombre LIKE 'P%'
//Lo de arriba es equivalente a SELECT nombre FROM persona WHERE nombre = 'p%'

//$direccion.idDireccion=?  Los ? se reemplazan con parámetros que van a venir en la función Query, n signos de pregunta, n parámetros
//Lo de arriba es equivalente a SELECT idDireccion FROM direccion WHERE idDireccion = ?

public class Hibernight {
	public static <T> String _query(Class<T> dtoClass, String xql){
		List <Token> token = getTokens(xql);
		//continuar desarrollo
		return "";
	}
	
	public static List <Token> getTokens(String xql){
		List <Token> tokenList;
		//devoler una lista de Token que nos va ayudar a hacer el SELECT
		return tokenList;
	}
}
