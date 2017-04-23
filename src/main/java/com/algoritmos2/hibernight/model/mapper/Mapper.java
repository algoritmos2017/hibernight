package com.algoritmos2.hibernight.model.mapper;

import com.algoritmos2.hibernight.model.annotations.Column;
import com.algoritmos2.hibernight.model.annotations.Table;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapper {

    private static final String PRIMARY_KEY = "primary_kay";
    private static final String TABLE_NAME = "table_name";


    public <T> void createTable(Class<T> clazz) {
        System.out.println(fieldsFor(clazz));
    }

    private <T> Map<String, String> fieldsFor(Class<T> clazz) {
        Map<String, String> fieldsByType = new HashMap<>();

        fieldsByType.put(TABLE_NAME, tableName(clazz));

        Arrays.asList(clazz.getDeclaredFields()).stream()
                .forEach(field -> {
                    List<Annotation> annotations = Arrays.asList(field.getAnnotations());

                    if (annotations.stream().anyMatch(a -> a.annotationType().getSimpleName().equals("Id"))) {
                        fieldsByType.put(PRIMARY_KEY, field.getName());
                    }

                    annotations.stream().forEach(a -> {
                        if (a.annotationType().getSimpleName().equals("Column")) {
                            fieldsByType.put(((Column) a).name(), field.getType().getSimpleName());
                        }
                    });
                });

        return fieldsByType;
    }

    private <T> String tableName(Class<T> clazz){
        return Arrays.asList(clazz.getDeclaredAnnotations()).stream()
                .filter(annotation -> annotation.annotationType().getSimpleName().equals("Table"))
                .findFirst()
                .map(a -> ((Table) a).name())
                .orElseThrow(() -> new RuntimeException("You need to write a Table name"));
    }

    private void _createTable(String tableName) {
        String sql = "CREATE TABLE " + tableName +
                "(id INTEGER not NULL, " +
                " person_name VARCHAR(255), " +
                " last VARCHAR(255), " +
                " age INTEGER, " +
                " PRIMARY KEY ( id ))";
    }
}
