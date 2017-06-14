package com.algoritmos2.hibernight.model.mapper;

import com.algoritmos2.hibernight.model.Direccion;
import com.algoritmos2.hibernight.model.Persona;
import com.algoritmos2.hibernight.model.QueryBuilder;
import com.algoritmos2.hibernight.model.annotations.Column;
import com.algoritmos2.hibernight.model.annotations.Id;
import com.algoritmos2.hibernight.model.annotations.Relation;
import com.algoritmos2.hibernight.model.annotations.Table;
import com.algoritmos2.hibernight.repository.Query;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.FileHandler;

public class Mapper {

    private static final String PRIMARY_KEY = "primary_key";
    private static final String TABLE_NAME = "table_name";
    private static final String COLUMN = "Column";
    private static final String TABLE = "Table";
    private static final String ID = "Id";
    private static final String WHERE = "WHERE";
    private static final String SELECT = "SELECT";
    private static Map<Class, String> types;
    private static Map<String, String> traduccion;

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

                if (nombreDeTabla != "") {
                    String nombreAtributo1 = obtenerFk(dtoClass, variable.getType());
                    String nombreAtributo2 = obtenerID(variable.getType());
                    atributosAux = " INNER JOIN " + nombreDeTabla;
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
                        fieldsByType.put(PRIMARY_KEY, field.getAnnotation(Column.class).name());
                    }

                    annotations.stream().forEach(a -> {
                        if (a.annotationType().getSimpleName().equals(COLUMN)) {
                            fieldsByType.put(((Column) a).name(), types.get(field.getType()));
                        }
                    });
                });

        return fieldsByType;
    }

    public static <T> String tableName(Class<T> unaClase) {
        Optional<Table> posibleNombre = Optional.ofNullable(unaClase.getAnnotation(Table.class));

        if (posibleNombre.isPresent())
            return posibleNombre.get().name();
        else
            return "";
    }

    public static <T> void obternerWhere(Class<T> clase, String xql, QueryBuilder queryBuilder, Object... args) {
        List<Class<?>> clasesATraducir = obtenerClasesDeRelaciones(clase);
        traduccion = traducirListaDeClases(clasesATraducir);
        String where = "", aux = "";
        char car;
        int argumentoN = -1, i = 0;

        xql += ' ';//Se le a�ade al final para que lo use como sentinela

        while (i < xql.length()) {
            car = xql.charAt(i);
            i++;

            if (Character.isLetterOrDigit(car)) {
                aux += car;
            }

            switch (car) {
                case ' ':
                    if (!aux.isEmpty()) {
                        where += analizarAux(aux);
                        aux = "";
                    }
                    where += ' ';
                    break;
                case '?':
                    argumentoN++;
                    try {
                        where += analizarArgumento(args[argumentoN].toString());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new Error("Faltan argumentos en el xql");
                    }
                    break;
                case '$':
                    break;
                case '.':
                    try {
                        where += analizarNombreDeTabla(aux, clase);
                    } catch (NoSuchFieldException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    aux = "";
                    where += '.';
                    break;
                case '=':
                    where += analizarAux(aux);
                    aux = "";
                    where += '=';
                    break;
            }
        }

        if ((argumentoN + 1) < args.length)
            throw new Error("Demasiados argumentos en el xql");

        queryBuilder.setWhere(where);
    }

    private static String analizarAux(String aux) {
        switch (aux) {
            case "or":
                return "OR";
            case "and":
                return "AND";
            default://Ser�an n�meros, campos o cadenas
                if (traduccion.containsKey(aux))//Si es un campo retornarlo traducido
                    return traduccion.get(aux);
                return analizarArgumento(aux);//No es un campo, pero es una cadena de letras o una constante
        }
    }

    private static String analizarArgumento(String argumento) {
        if (esCadenaAlfnum(argumento))//Si es alfanum�rica le pone las comillas
            return "\"" + argumento + "\"";
        return argumento;//Si no, la retorna como una constante
    }

    private static boolean esCadenaAlfnum(String cadena) {
        boolean esCadenaDeNumeros = true;
        for (int i = 0; i < cadena.length(); ++i) {
            if (!Character.isDigit(cadena.charAt(i))) {
                esCadenaDeNumeros = false;
                break;
            }
        }
        if (esCadenaDeNumeros)
            return false;

        for (int i = 0; i < cadena.length(); ++i) {
            char caracter = cadena.charAt(i);

            if (!Character.isLetterOrDigit(caracter)) {
                throw new Error("XQL contiene caracteres incorrectos");
            }
        }
        return true;
    }

    private static <T> String analizarNombreDeTabla(String fieldName, Class<T> clase) throws NoSuchFieldException, SecurityException {

        Field atributoBuscado = clase.getDeclaredField(fieldName);

        return tableName(atributoBuscado.getType());
    }

    public static <T> Object getObjectFrom(Class<T> dtoClass, ResultSet rs, Connection connection) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor = dtoClass.getConstructor();
        Object parentClassInstance = constructor.newInstance();

        Optional<Field> relationedField = Arrays.stream(dtoClass.getDeclaredFields()).filter(field -> null != field.getAnnotation(Relation.class)).findFirst();
        if(relationedField.isPresent()){
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(dtoClass);
            parentClassInstance = enhancer.create(dtoClass, new MethodInterceptor() {
                        @Override
                        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                            Class<?> collectionConcreteClass = Class.forName(((ParameterizedTypeImpl) relationedField.get().getGenericType()).getActualTypeArguments()[0].getTypeName());
                            if (method.getName().equalsIgnoreCase("get" + relationedField.get().getName())) {
                                System.out.println("Interceptando ....");
                                Field primaryKeyField = Arrays.stream(dtoClass.getDeclaredFields()).filter(field -> null != field.getAnnotation(Id.class)).findFirst().orElseGet(null);
                                return Query.query(connection, collectionConcreteClass, "$" + tableName(Class.forName(o.getClass().getTypeName().split("\\$")[0])) + "." + primaryKeyField.getName() + "=?", 1);
                            } else {
                                return methodProxy.invokeSuper(o, objects);
                            }
                        }
                    });
        }

        Object instance = parentClassInstance;

        Arrays.stream(dtoClass.getDeclaredFields())
                .forEach(field -> {
                    try {
                        if (null != field.getAnnotation(Column.class)) {
                            if (null != field.getType().getAnnotation(Table.class)) {
                                Object classObject = getObjectFrom(field.getType(), rs, connection);

                                Field declaredField = dtoClass.getDeclaredField(field.getName());
                                declaredField.setAccessible(Boolean.TRUE);
                                declaredField.set(instance, classObject);
                            } else {
                                Class<?> fieldClazz = Class.forName(field.getType().getName());
                                Constructor<?> ctor = fieldClazz.getConstructor(String.class);
                                Object object = ctor.newInstance(new Object[]{
                                        rs.getString(dtoClass.getAnnotation(Table.class).name() + "." + field.getAnnotation(Column.class).name())
                                });

                                Field declaredField = dtoClass.getDeclaredField(field.getName());
                                declaredField.setAccessible(Boolean.TRUE);
                                declaredField.set(instance, object);
                            }
                        }
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });

        return parentClassInstance;

    }

    public static <T> Object createInterceptedObject(Object o, Connection connection) {
        Object result = o;
        if(Arrays.stream(o.getClass().getDeclaredFields()).anyMatch(field -> null != field.getAnnotation(Relation.class))){
            Enhancer enhancer = new Enhancer();

            Arrays.stream(o.getClass().getDeclaredFields()).forEach(field -> {
                if (null != field.getAnnotation(Relation.class)) {

                    //Tomo la clase que esta dentro de la coleccion, para poder armar la query
                    Field fieldPrimaryKeyName = Arrays.stream(o.getClass().getDeclaredFields())
                            .filter(pkfield -> null != pkfield.getAnnotation(Id.class))
                            .findFirst()
                            .orElseGet(null);

                    enhancer.create(o.getClass(), new MethodInterceptor() {
                        @Override
                        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                            Class<?> collectionConcreteClass = Class.forName(((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
                            if (method.getName().equalsIgnoreCase("get" + field.getName())) {
                                //System.out.println("Interceptando ...." + collectionClass);
                                //System.out.println(Query._query(collectionClass, "$" + tableName(clazz) + "." + fieldPrimaryKeyName.getName() + "=?", 1));
                                return Query.query(connection, collectionConcreteClass, "$" + tableName(o.getClass()) + "." + fieldPrimaryKeyName.getName() + "=?", fieldPrimaryKeyName.get(o));
                            } else {
                                return methodProxy.invokeSuper(o, objects);
                            }
                        }
                    });
                }
            });

            result = enhancer;
        }

        return result;
    }

    /* M�todo muy �ltil recibe una clase A y devuelve un set ordenado con todas las
       clases con las que se relaciona esa clase A, incluyendo la misma clase A
       Ej: m�tododo(Persona.class) devuelde Persona, Direcci�n, Ocupaci�n, etc */
    public static <T> List<Class<?>> obtenerClasesDeRelaciones(Class<T> claseAEvaluar) {
        List<Class<?>> clasesEvaluadas = new ArrayList<>();
        List<Class<?>> clasesAEvaluar = new ArrayList<>();

        //Vamos a evaluar la clase actual as� que la a�adimos en el set sin repeticion
        clasesAEvaluar.add(claseAEvaluar);

        //Ahora llamamos al m�todo que nos trae el set de clases y lo retornamos
        clasesEvaluadas = obtenerClasesDeRelacionesRecursivo(claseAEvaluar, clasesEvaluadas, clasesAEvaluar);
        return clasesEvaluadas;
    }

    private static List<Class<?>> obtenerClasesDeRelacionesRecursivo
            (Class<?> claseActual,
             List<Class<?>> clasesEvaluadas,
             List<Class<?>> clasesAEvaluar) {
        Field[] variables = claseActual.getDeclaredFields();
        Class<?> claseDeLaVariable;

        //Buscamos todas las variables que sean de una tabla y las guardamos para evaluar
        for (Field variable : variables) {
            claseDeLaVariable = variable.getType();

            if (!tableName(claseDeLaVariable).isEmpty() &&
                    !clasesEvaluadas.contains(claseDeLaVariable))

                clasesAEvaluar.add(claseDeLaVariable);
        }

    /*  Ya obtuvimos todas las relaciones a otras tablas de esta clase
        as� que las y la sacamos de las que se van a evaluar y de las evaluadas */
        clasesAEvaluar.remove(claseActual);
        clasesEvaluadas.add(claseActual);

        //Llamada recursiva a este m�todo si clasesAEvaluar a�n contiene elementos
        if (!clasesAEvaluar.isEmpty()) {//Si no est� vac�o
            claseActual = clasesAEvaluar.get(0);//Obtenemos un elemento de las que son a evaluar
            obtenerClasesDeRelacionesRecursivo(claseActual, clasesEvaluadas, clasesAEvaluar);
        }

        return clasesEvaluadas;
    }

    /*Se le pasa una clase y devuelve un HashMap con los nombres de los atributos
      en objetos y el nombre de los atributos de las tablas en paralelo.
      Tambi�n se a�dade el nombre de la clase y el nombre de la tabla
      Ej: M�todo(Persona) devuelve [<idPersona, id_persona>,
      <direccion, id_direccion>, <Persona, person>, etc]  */
    public static <T> Map<String, String> traducirDeObjetosARelacional(Class<T> clase) {
        Field[] variables = clase.getDeclaredFields();
        Map<String, String> nombreDelProgramadorYDeLaTabla = new HashMap<>();

        if (!tableName(clase).isEmpty())
            nombreDelProgramadorYDeLaTabla.put(clase.getSimpleName(),
                    tableName(clase));

        for (Field variable : variables) {
            Optional<Column> posibleAnotacion = Optional.ofNullable(variable.getAnnotation(Column.class));

            if (posibleAnotacion.isPresent())
                nombreDelProgramadorYDeLaTabla.put(variable.getName(),
                        posibleAnotacion.get().name());
        }

        return nombreDelProgramadorYDeLaTabla;
    }

    //Lo mismo que arriba pero para una lista de clases
    private static Map<String, String> traducirListaDeClases(List<Class<?>> clases) {
        Map<String, String> traduccion = new HashMap<>();

        for (Class<?> clase : clases)
            traduccion.putAll(traducirDeObjetosARelacional(clase));

        return traduccion;
    }

    private static void main(String args[]) {
        System.out.println(traducirDeObjetosARelacional(Persona.class));
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
