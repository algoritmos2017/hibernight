package com.algoritmos2.hibernight.model.mapper;

import com.algoritmos2.hibernight.model.QueryBuilder;
import com.algoritmos2.hibernight.model.annotations.Column;
import com.algoritmos2.hibernight.model.annotations.Id;
import com.algoritmos2.hibernight.model.annotations.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import org.mockito.internal.creation.jmock.ClassImposterizer.ClassWithSuperclassToWorkAroundCglibBug;

public class Mapper {

    private static final String PRIMARY_KEY = "primary_key";
    private static final String TABLE_NAME = "table_name";
    private static final String COLUMN = "Column";
    private static final String TABLE = "Table";
    private static final String ID = "Id";
    private static final String WHERE = "WHERE";
    private static final String SELECT = "SELECT";
    private static Map<Class, String> types;
    private static int recorrido = 0;//Para el autómata
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

    public static <T> String tableName(Class<T> unaClase) {
        Optional<Table> posibleNombre = Optional.ofNullable(unaClase.getAnnotation(Table.class));
        
    	if(posibleNombre.isPresent())
    		return posibleNombre.get().name();
    	else
    		return "";
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
    
    public static <T> void obternerWhere(Class<T> clase, String xql,QueryBuilder queryBuilder, Object... args){
    	List <Class<?>> clasesATraducir = obtenerClasesDeRelaciones(clase);
    	Map <String, String> traduccion = traducirListaDeClases(clasesATraducir);
    	String where = "";
    	Token token;
    	int argumentoN = 0;
    	
    	while(recorrido <= xql.length()){
    		token = miniAutomata(xql);
    		
    		switch(token.getEstado()){
    			case 5: throw new Error("Formato incorrecto en el XQL");
    			case 2:
    				where += traduccion.get(token.getValor());
    				break;
    			case 3:
    				where += token.getValor();
    				break;
    			case 4:
    				//where += args[argumentoN];
    				argumentoN++;
    				break;
    			case 6:
    				where += token.getValor();
    				break;
    		}
    	}
    	
    	queryBuilder.setWhere(where);
    }
    
    private static Token miniAutomata(String xql){
    	Token token = new Token();
    	int estado = 0;
    	char caracter;
    	String aux = "";
    	final int[][] TT = {{1,3,0,4}, {5,2,5,5}};
    	
    	caracter = avanzar(xql);
		
		if(Character.isLetterOrDigit(caracter))
			aux += caracter;
		
		estado = TT [estado][columna(caracter)];
    	
    	while(!(estado == 3 || estado == 2 || estado == 4) && (caracter = avanzar(xql)) != '\0'){
    		
    		if(Character.isLetterOrDigit(caracter))
    			aux += caracter;
    		
    		estado = TT [estado][columna(caracter)];
    		
    	}
    	
    	if(estado == 3){
    		aux = "";
    		aux += xql.charAt(recorrido);
    		token.setValor(aux);
    		token.setEstado(3);
    	}
    	
    	if(estado == 4){
    		aux = "";
    		aux += "?";
    		token.setValor(aux);
    		token.setEstado(4);
    	}
    	
    	if(estado == 2){
    		recorrido--;
    		
    		if(aux.equals("and")){
    			token.setValor("AND");
    			token.setEstado(6);
    		}
    		if(aux.equals("or")){
    			token.setValor("OR");
    			token.setEstado(6);
    		}
    		else{
    			token.setValor(aux);
        		token.setEstado(2);
    		}
    	}
    	return token;
    }
    
    private static char avanzar(String xql){
    	char caracter = '\0';
    	
    	if(xql.length() > recorrido){
    		caracter = xql.charAt(recorrido);
        	recorrido++;
    	}
    	return caracter;
    }
    
    private static int columna(char caracter){
    	if(caracter == ' ' || caracter == '$') return 3;
    	if(Character.isLetterOrDigit(caracter)) return 1;
    	return 2;
    }
    
 /* Método muy últil recibe una clase A y devuelve un set ordenado con todas las
    clases con las que se relaciona esa clase A, incluyendo la misma clase A
    Ej: métododo(Persona.class) devuelde Persona, Dirección, Ocupación, etc */
    public static <T> List<Class<?>> obtenerClasesDeRelaciones(Class<T> claseAEvaluar){
    	List <Class<?>> clasesEvaluadas = new ArrayList<>();
    	List <Class<?>> clasesAEvaluar = new ArrayList<>();
    	
    	//Vamos a evaluar la clase actual así que la añadimos en el set sin repeticion
    	clasesAEvaluar.add(claseAEvaluar);
    	
    	//Ahora llamamos al método que nos trae el set de clases y lo retornamos
    	clasesEvaluadas = obtenerClasesDeRelacionesRecursivo(claseAEvaluar, clasesEvaluadas, clasesAEvaluar);
    	return clasesEvaluadas;
    }
    
    private static List<Class<?>> obtenerClasesDeRelacionesRecursivo
    					(Class<?> claseActual,
    					List <Class<?>> clasesEvaluadas,
    					List <Class<?>> clasesAEvaluar)
    {
    	Field [] variables = claseActual.getDeclaredFields();
    	Class<?> claseDeLaVariable;
    	
    	//Buscamos todas las variables que sean de una tabla y las guardamos para evaluar
    	for(Field variable:variables){
    		claseDeLaVariable = variable.getType();
    		
    		if(!tableName(claseDeLaVariable).isEmpty() &&
    		   !clasesEvaluadas.contains(claseDeLaVariable))
    			
    			clasesAEvaluar.add(claseDeLaVariable);
    	}
    	
    /*  Ya obtuvimos todas las relaciones a otras tablas de esta clase
    	así que las y la sacamos de las que se van a evaluar y de las evaluadas */
    	clasesAEvaluar.remove(claseActual);
    	clasesEvaluadas.add(claseActual);
    	
    	//Llamada recursiva a este método si clasesAEvaluar aún contiene elementos
    	if(!clasesAEvaluar.isEmpty()){//Si no está vacío
    		claseActual = clasesAEvaluar.get(0);//Obtenemos un elemento de las que son a evaluar
        	obtenerClasesDeRelacionesRecursivo(claseActual, clasesEvaluadas, clasesAEvaluar);
    	}
    	
    	return clasesEvaluadas;
    }
    
    /*Se le pasa una clase y devuelve un HashMap con los nombres de los atributos 
      en objetos y el nombre de los atributos de las tablas en paralelo.
      También se añdade el nombre de la clase y el nombre de la tabla
      Ej: Método(Persona) devuelve [<idPersona, id_persona>, 
      <direccion, id_direccion>, <Persona, person>, etc]  */
    private static <T> Map<String, String> traducirDeObjetosARelacional(Class<T> clase){
    	Field [] variables = clase.getDeclaredFields();
    	Map <String, String> nombreDelProgramadorYDeLaTabla = new HashMap<>();
    	
    	if(!tableName(clase).isEmpty())
    		nombreDelProgramadorYDeLaTabla.put(clase.getSimpleName(), 
    										   tableName(clase));
    	
    	for(Field variable:variables){
    		Optional <Column> posibleAnotacion = Optional.ofNullable(variable.getAnnotation(Column.class));
    		
    		if(posibleAnotacion.isPresent())
    			nombreDelProgramadorYDeLaTabla.put(variable.getName(),
    											   posibleAnotacion.get().name());
    	}
    	
    	return nombreDelProgramadorYDeLaTabla;
    }
    
    //Lo mismo que arriba pero para una lista de clases
    public static Map<String, String> traducirListaDeClases(List<Class<?>> clases){
    	Map <String, String> traduccion = new HashMap<>();
    	
    	for(Class<?> clase:clases)
    		traduccion.putAll(traducirDeObjetosARelacional(clase));
    	
    	return traduccion;
    }
}
