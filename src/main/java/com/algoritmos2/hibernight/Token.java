package com.algoritmos2.hibernight;

public class Token {
	public enum TIPO {SELECT, WHERE, FROM};
	private TIPO tipo; //Si va en la parte del select, en la del where o en la del from
	private String token; //el nombre de la columna o la restricción
	
	public Token(TIPO tipo, String token) {
		this.tipo = tipo;
		this.token = token;
	}
	
	public TIPO getTipo() {
		return tipo;
	}
	public void setTipo(TIPO tipo) {
		this.tipo = tipo;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
