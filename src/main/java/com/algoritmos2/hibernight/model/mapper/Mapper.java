package com.algoritmos2.hibernight.model.mapper;

import com.algoritmos2.hibernight.model.QueryBuilder;
import com.algoritmos2.hibernight.model.annotations.Column;
import com.algoritmos2.hibernight.model.annotations.Id;
import com.algoritmos2.hibernight.model.annotations.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class Mapper {

    private static final String PRIMARY_KEY = "primary_key";
    private static final String TABLE_NAME = "table_name";
    private static final String COLUMN = "Column";
    private static final String TABLE = "Table";
    private static final String ID = "Id";
    private static final String WHERE = "WHERE";
    private static final String SELECT = "SELECT";
    private static Map<Class, String> types;

    public Mapper() {
        this.types = new HashMap() {{
            put(String.class, "VARCHAR(255)");
            put(Integer.class, "INTEGER");
        }};
    }

    public static String obtenerFk(Class<?> dtoClass, Class<?> dtoClass2) {
        String nombreTabla = null;
        String fkId = null;
        final Field[] variables = dtoClass.getDeclaredFields();

        for (final Field variable : variables) {
            final Column anotacionObtenida = variable.getAnnotation(Column.class);

            if (anotacionObtenida != null) {
                nombreTabla = tableName(variable.getType());

                if (nombreTabla != null && dtoClass2 == variable.getType()) {
                    fkId = anotacionObtenida.name();
                }
            }
        }

        return fkId;
    }

    public static String obtenerID(Class<?> dtoClass) {
        String nombreAtributo = null;
        final Field[] variables = dtoClass.getDeclaredFields();

        for (final Field variable : variables) {
            final Id anotacionObtenida = variable.getAnnotation(Id.class);

            if (anotacionObtenida != null) {
                final Column anotacionObtenida2 = variable.getAnnotation(Column.class);
                nombreAtributo = anotacionObtenida2.name();
            }
        }
        return nombreAtributo;
    }

    public static void obtenerCampos(Class<?> dtoClass, QueryBuilder queryObjet) {
        String nombreDeTabla1 = tableName(dtoClass);
        final Field[] variables = dtoClass.getDeclaredFields();
        String atributosAux = "";
        String nombreDeTabla = "";

        for (final Field variable : variables) {
            final Column anotacionObtenida = variable.getAnnotation(Column.class);
            nombreDeTabla = tableName(variable.getType());

            if (anotacionObtenida != null) {
                String nombreAtributo = anotacionObtenida.name();
                atributosAux = nombreDeTabla1 + "." + nombreAtributo;
                queryObjet.addColumn(atributosAux);

                //TODO: deberiamos cortar cuando es LAZY ?
                if (nombreDeTabla != "") {
                    String nombreAtributo1 = obtenerFk(dtoClass, variable.getType());
                    String nombreAtributo2 = obtenerID(variable.getType());
                    atributosAux = "INNER JOIN " + nombreDeTabla + " " + nombreDeTabla;
                    atributosAux += " ON " + nombreDeTabla1 + "." + nombreAtributo1 + "=" + nombreDeTabla + "." + nombreAtributo2;
                    queryObjet.addJoin(atributosAux);
                    obtenerCampos(variable.getType(), queryObjet);
                }
            }
        }
    }

    public static <T> Map<String, String> fieldsFor(Class<T> clazz) {
        Map<String, String> fieldsByType = new HashMap<>();

        Arrays.asList(clazz.getDeclaredFields()).stream()
                .forEach(field -> {
                    List<Annotation> annotations = Arrays.asList(field.getAnnotations());

                    if (annotations.stream().anyMatch(a -> a.annotationType().getSimpleName().equals(ID))) {
                        fieldsByType.put(PRIMARY_KEY, field.getName());
                    }

                    annotations.stream().forEach(a -> {
                        if (a.annotationType().getSimpleName().equals(COLUMN)) {
                            fieldsByType.put(((Column) a).name(), types.get(field.getType()));
                        }
                    });
                });

        return fieldsByType;
    }

    public static <T> String tableName(Class<T> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(Table.class))
                .orElseThrow(() -> new RuntimeException("Class must have Table name"))
                .name();
        /*
        return Arrays.asList(clazz.getDeclaredAnnotations()).stream()
                .filter(annotation -> annotation.annotationType().getSimpleName().equals(TABLE))
                .findFirst()
                .map(a -> ((Table) a).name())
                .orElseThrow(() -> new RuntimeException("You need to write a Table name"));
                         */
    }

    public <T> String createTableQuery(Class<T> clazz) {
        Map<String, String> fieldsByType = new HashMap<>();

        fieldsByType.put(TABLE_NAME, tableName(clazz));
        fieldsByType.putAll(fieldsFor(clazz));
        return _createTable(fieldsByType);
    }

    private String _createTable(Map<String, String> fields) {
        StringBuilder builder = new StringBuilder();

        String query = "CREATE TABLE ?table_name (?statement PRIMARY KEY (?primary_key))";

        fields.entrySet().stream().forEach(entry -> {
            if (!Arrays.asList(TABLE_NAME, PRIMARY_KEY).contains(entry.getKey()))
                builder.append(entry.getKey() + " " + entry.getValue() + ", ");
        });

        String transformedQuery = query.replace("?table_name", fields.get(TABLE_NAME))
                .replace("?primary_key", fields.get(PRIMARY_KEY))
                .replace("?statement", builder.toString());

        return transformedQuery;
    }

}
