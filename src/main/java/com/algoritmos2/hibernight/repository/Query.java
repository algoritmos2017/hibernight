package com.algoritmos2.hibernight.repository;

import com.algoritmos2.hibernight.model.QueryBuilder;
import com.algoritmos2.hibernight.model.mapper.Mapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {

    // Retorna: el SQL correspondiente a la clase dtoClass acotado por xql
    private static <T> String _query(Class<T> dtoClass, String xql) {
        QueryBuilder queryBuilder = new QueryBuilder();
        List<String> fields = new ArrayList();
        Pattern pattern = Pattern.compile("\\$(.*?)=\\?");
        Matcher matcher = pattern.matcher(xql);

        while (matcher.find()) {
            fields.add(matcher.group(1));
        }

        queryBuilder.setTablaName(Mapper.tableName(dtoClass));
        Mapper.obtenerCampos(dtoClass, queryBuilder);

        String sql = "";
        sql += "SELECT ";

        for (final String campo : queryBuilder.getColumns()) {
            sql += campo + ", ";
        }

        sql += "FROM " + queryBuilder.getTablaName() + " " + queryBuilder.getTablaName().charAt(0) + " ";

        for (final String join : queryBuilder.getJoins()) {
            sql += "\n";
            sql += join + " ";
        }

        sql += "\n";
        sql += "WHERE " + xql;

        return sql;
    }

    // Invoca a: _query para obtener el SQL que se debe ejecutar
    // Retorna: una lista de objetos de tipo T
    public static <T> List<T> query(Connection con, Class<T> dtoClass, String xql, Object... args) throws SQLException {
        String realQuery = _query(dtoClass, xql);
        Statement stmt = null;

        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Retorna: una fila identificada por id o null si no existe
    // Invoca a: query
    private static <T> T find(Connection con, Class<T> dtoClass, Object id) {
        return null;
    }

    // Retorna: una todasa las filas de la tabla representada por dtoClass
    // Invoca a: query
    private static <T> List<T> findAll(Connection con, Class<T> dtoClass) {
        return null;
    }

    // Retorna: el SQL correspondiente a la clase dtoClass acotado por xql
    public static <T> String _update(Class<T> dtoClass, String xql) {
        return null;
    }

    // Invoca a: _update para obtener el SQL que se debe ejecutar
    // Retorna: la cantidad de filas afectadas luego de ejecutar el SQL
    public static int update(Connection con, Class<?> dtoClass, String xql, Object... args) {
        return 0;
    }

    // Invoca a: update
    // Que hace?: actualiza todos los campos de la fila identificada por el id de dto
    // Retorna: Cuantas filas resultaron modificadas (deberia: ser 1 o 0)
    public static int update(Connection con, Object dto) {
        return 0;
    }

    // Retorna: el SQL correspondiente a la clase dtoClass acotado por xql
    public static String _delete(Class<?> dtoClass, String xql) {
        return null;
    }

    // Invoca a: _delete para obtener el SQL que se debe ejecutar
    // Retorna: la cantidad de filas afectadas luego de ejecutar el SQL
    public static int delete(Connection con, Class<?> dtoClass, String xql, Object... args) {
        return 0;
    }

    // Retorna la cantidad de filas afectadas al eliminar la fila identificada por id
    // (deberia ser: 1 o 0)
    // Invoca a: delete
    public static int delete(Connection con, Class<?> dtoClass, Object id) {
        return 0;
    }

    // Retorna: el SQL correspondiente a la clase dtoClass
    public static String _insert(Class<?> dtoClass) {
        return null;
    }

    // Invoca a: _insert para obtener el SQL que se debe ejecutar
    // Retorna: la cantidad de filas afectadas luego de ejecutar el SQL
    public static int insert(Connection con, Object dto) {
        return 0;
    }

}
