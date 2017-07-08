package com.algoritmos2.hibernight.repository;

import com.algoritmos2.hibernight.model.QueryBuilder;
import com.algoritmos2.hibernight.model.annotations.Column;
import com.algoritmos2.hibernight.model.annotations.Id;
import com.algoritmos2.hibernight.model.annotations.Table;
import com.algoritmos2.hibernight.model.mapper.Mapper;
import com.mysql.jdbc.PreparedStatement;
import com.algoritmos2.hibernight.model.mapper.UBean;
import javax.sound.midi.SysexMessage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Query {

	// Retorna: el SQL correspondiente a la clase dtoClass acotado por xql
	public static <T> String _query(Class<T> dtoClass, String xql) throws NoSuchFieldException, SecurityException {
		QueryBuilder queryBuilder = new QueryBuilder();

		queryBuilder.setTablaName(Mapper.tableName(dtoClass));
		Mapper.obtenerCampos(dtoClass, queryBuilder);

		String sql = "";
		sql += "SELECT ";

		for (final String campo : queryBuilder.getColumns()) {
			sql += campo + ", ";
		}
		// Sacar esta villereada
		sql = sql.substring(0, sql.length() - 2) + " ";
		
		sql += " FROM " + queryBuilder.getTablaName();
		
		List<String> joins = queryBuilder.getJoins();
		
		for (final String join : joins) {
			sql += join + " ";
		}
		
		Mapper.obternerWhere(dtoClass, xql, queryBuilder);
		if (!queryBuilder.getWhere().equals(" ")) {
			sql += " WHERE " + queryBuilder.getWhere();
		}
		
		return sql;
	}

	// Invoca a: _query para obtener el SQL que se debe ejecutar
	// Retorna: una lista de objetos de tipo T
	public static <T> List<T> query(Connection con, Class<T> dtoClass, String xql, Object... args)
			throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException,
			NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, SecurityException {
		// String realQuery = "Select * from Persona inner join direccion on
		// Persona.id_direccion=direccion.id_direccion inner join ocupacion on
		// Persona.id_ocupacion=ocupacion.id_ocupacion inner join tipo_ocupacion
		// on ocupacion.id_tipo_ocupacion=tipo_ocupacion.id_tipoocupacion";
		String realQuery = _query(dtoClass, xql);

		for(int i = 0;i < args.length; i++){
			if(args[i].getClass().equals(String.class)){
				args[i] = "\'" + args[i] + "\'";
		}}
		  	
		realQuery = String.format(realQuery.replace("?", "%s"),args);
		 
		List<Object> result = new ArrayList();
		Statement stmt = null;
		//System.out.println(realQuery);
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Supuestamente se le tienen que settear los argumentos al statement con setObject
		
		ResultSet rs = stmt.executeQuery(realQuery);

		/*
		 * Class<?> clazz = Class.forName(dtoClass.getName()); Constructor<?>
		 * constructor = clazz.getConstructor(); Object objectInstance =
		 * constructor.newInstance();
		 */

		while (rs.next()) {

			result.add(Mapper.getObjectFrom(dtoClass, rs, con));
		}

		return (List<T>) result;
	}
	
	// Retorna: una fila identificada por id o null si no existe
	// Invoca a: query
	public static <T> T find(Connection con, Class<T> dtoClass, Object id)
			throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException,
			ClassNotFoundException, SQLException, SecurityException, NoSuchFieldException {

		Field[] atributos = dtoClass.getDeclaredFields();
		String atributo = null;
		for (Field f : atributos) {
			if (null != f.getDeclaredAnnotation(Id.class)
					&& f.getDeclaredAnnotation(Id.class).strategy() == Id.IDENTITY) {
				atributo = f.getName();
			}
		}
		String xql = "$" + atributo + " =? ";
		//System.out.println(xql);
		List<T> lista = Query.query(con, dtoClass, xql, id);
		return lista.isEmpty() ? null : lista.get(0);
	}

	// Retorna: una todasa las filas de la tabla representada por dtoClass
	// Invoca a: query
	public static <T> List<T> findAll(Connection con, Class<T> dtoClass)
			throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException,
			ClassNotFoundException, SQLException {
		List<T> lista = Query.query(con, dtoClass, "");
		return lista.isEmpty() ? null : lista;
	}

	// Retorna: el SQL correspondiente a la clase dtoClass acotado por xql
	public static <T> String _update(Class<T> dtoClass, String xql) {
		String columns = xql.split("set")[1].split("where")[0].replaceAll("\\s+","");
		String criterio = xql.split("set")[1].split("where")[1].replaceAll("\\s+","");
		
		List<String> columnNames = Mapper.fromFieldsToTableColumns(columns, dtoClass);
		List<String> criteriaColumnNames = Mapper.fromFieldsToTableColumns(criterio, dtoClass);

		StringBuilder fieldsQuery = new StringBuilder("UPDATE " + dtoClass.getAnnotation(Table.class).name() + " SET ");
		StringBuilder names = new StringBuilder();
		StringBuilder criteria = new StringBuilder();

		columnNames.stream().forEach(name -> {
			names.append(name).append("=").append("?").append(",");
		});

		criteriaColumnNames.stream().forEach(crit -> {
			criteria.append(crit).append("=").append("?").append(",");
		});

		fieldsQuery.append(names.toString().substring(0, names.toString().length() - 1));
		fieldsQuery.append(" WHERE ");
		fieldsQuery.append(criteria.toString().substring(0, criteria.toString().length() - 1));

		return fieldsQuery.toString();
	}
	
 
	// Invoca a: _update para obtener el SQL que se debe ejecutar
	// Retorna: la cantidad de filas afectadas luego de ejecutar el SQL
	public static int update(Connection con, Class<?> dtoClass, String xql, Object... args) throws SQLException {
		//List<String> values = Arrays.stream(args).map(name -> Mapper.analizarArgumento((String) name))
		//		.collect(Collectors.toList());
		for(int i = 0;i < args.length; i++){
			if(args[i].getClass().equals(String.class)){
				args[i] = "\'" + args[i] + "\'";
		}}

		String query = String.format(_update(dtoClass, xql).replace("?", "%s"),args);

		Statement stmt = null;
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Update finalizado: " + query);
		return stmt.executeUpdate(query);
	}

	// Invoca a: update
	// Que hace?: actualiza todos los campos de la fila identificada por el id
	// de dto
	// Retorna: Cuantas filas resultaron modificadas (deberia: ser 1 o 0)
	public static int update(Connection con, Object dto) throws NoSuchFieldException, SecurityException, SQLException {
		
		String xql="set $";
		String where = " where $";
		//String[] att = UBean.getAttNames(dto.getClass());
		Field[] fields = dto.getClass().getDeclaredFields();
		List<String> att = null;
		for(Field f: fields){
			att.add(f.getName());
		}
		List <Object> args1 = null;
		for(String s : att){
			
			if(null != dto.getClass().getDeclaredField(s).getAnnotation(Column.class)){
				xql= xql.concat(s + "=?,$");
				System.out.println(xql);
				if(dto.getClass().getDeclaredField(s).isAnnotationPresent(Id.class) && dto.getClass().getDeclaredField(s).getAnnotation(Id.class).strategy() == Id.IDENTITY){
					where = where.concat(s+ "=?");
				}
				args1.add(UBean.invokeGetter(dto,s));
				
			}
		}
		xql = xql.substring(0,xql.length()-2);
		xql.concat(where);
		Object[] args = args1.toArray();
		
		
		return Query.update(con,dto.getClass(),xql,args);
		}

	// Retorna: el SQL correspondiente a la clase dtoClass acotado por xql
	public static String _delete(Class<?> dtoClass, String xql) {
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder.setTablaName(Mapper.tableName(dtoClass));
		
		Mapper.obternerWhere(dtoClass, xql, queryBuilder);
		Mapper.analizarTablasWhere(dtoClass, queryBuilder);
		
		
		StringBuilder fieldsQuery = new StringBuilder("DELETE ");
		
		fieldsQuery.append(queryBuilder.getTablaName().toString()+ " FROM ");
		fieldsQuery.append(queryBuilder.getTablaName().toString());	
		
		List<String> joins = queryBuilder.getJoins();
		for (final String join : joins) {
			fieldsQuery.append(join + " ");
		}
		
		fieldsQuery.append(" WHERE " +queryBuilder.getWhere());	
		
		return fieldsQuery.toString();
	}

	// Invoca a: _delete para obtener el SQL que se debe ejecutar
	// Retorna: la cantidad de filas afectadas luego de ejecutar el SQL
	public static int delete(Connection con, Class<?> dtoClass, String xql, Object... args) throws SQLException {
		List<String> values = Arrays.stream(args).map(name -> Mapper.analizarArgumento((String) name))
				.collect(Collectors.toList());

		String query = String.format(_delete(dtoClass, xql).replace("?", "%s"), values.toArray());

		Statement stmt = null;
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println(query);
		return stmt.executeUpdate(query);
		
	}
	
	// Retorna la cantidad de filas afectadas al eliminar la fila identificada
	// por id
	// (deberia ser: 1 o 0)
	// Invoca a: delete
	public static int delete(Connection con, Class<?> dtoClass, Object id) {
		return 0;
	}
	
	// Retorna: el SQL correspondiente a la clase dtoClass
	public static String _insert(Object dto) {
		return Mapper._insert(dto);
	}
	
	// Invoca a: _insert para obtener el SQL que se debe ejecutar
	// Retorna: la cantidad de filas afectadas luego de ejecutar el SQL
	public static int insert(Connection con, Object dto) {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Error("La connection es incorrecta");
		}
		try {
			stmt.executeUpdate(_insert(dto));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
		return 0;
	}
	
}
