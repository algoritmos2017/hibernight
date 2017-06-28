package com.algoritmos2.hibernight.model.mapper;

import com.algoritmos2.hibernight.model.Direccion;
import com.algoritmos2.hibernight.model.Ocupacion;
import com.algoritmos2.hibernight.model.Persona;
import com.algoritmos2.hibernight.model.QueryBuilder;
import com.algoritmos2.hibernight.model.annotations.Column;
import com.algoritmos2.hibernight.model.annotations.Id;
import com.algoritmos2.hibernight.model.annotations.Table;
import com.algoritmos2.hibernight.repository.Query;
import com.google.gson.Gson;

import net.sf.cglib.proxy.Enhancer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
		this.types = new HashMap() {
			{
				put(String.class, "VARCHAR(255)");
				put(Integer.class, "INTEGER");
			}
		};
	}

	private static List<String> fromXqlToFields(String xql) {
		List<String> fields = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\$(.*?)=\\?");
		Matcher matcher = pattern.matcher(xql);

		while (matcher.find()) {
			fields.add(matcher.group(1));
		}
		return fields;
	}

	public static <T> List<String> fromFieldsToTableColumns(String xql, Class<T> clazz) {
		List<String> fields = fromXqlToFields(xql);
		List<String> fieldTableNames = new ArrayList<>();

		fields.stream().forEach(field -> {
			try {
				Field f = clazz.getDeclaredField(field);
                fieldTableNames.add(f.getAnnotation(Column.class).name());
			} catch (NoSuchFieldException e) {
				throw new RuntimeException("No hay una columna con ese nombre");
			}
		});

		return fieldTableNames;
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
					atributosAux += " ON " + nombreDeTabla1 + "." + nombreAtributo1 + "=" + nombreDeTabla + "."
							+ nombreAtributo2;
					queryObjet.addJoin(atributosAux);
					obtenerCampos(variable.getType(), queryObjet);
				}
			}
		}
	}

	public static <T> Map<String, String> fieldsFor(Class<T> clazz) {
		Map<String, String> fieldsByType = new HashMap<>();

		Arrays.asList(clazz.getDeclaredFields()).stream().forEach(field -> {
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

		xql += ' ';// Se le aï¿½ade al final para que lo use como sentinela

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
		default:// Serï¿½an nï¿½meros, campos o cadenas
			if (traduccion.containsKey(aux))// Si es un campo retornarlo
											// traducido
				return traduccion.get(aux);
			return analizarArgumento(aux);// No es un campo, pero es una cadena
											// de letras o una constante
		}
	}

	public static String analizarArgumento(String argumento) {
		if (esCadenaAlfnum(argumento))// Si es alfanumï¿½rica le pone las
										// comillas
			return "\'" + argumento + "\'";
		return argumento;// Si no, la retorna como una constante
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

	private static <T> String analizarNombreDeTabla(String fieldName, Class<T> clase)
			throws NoSuchFieldException, SecurityException {

		Field atributoBuscado = clase.getDeclaredField(fieldName);

		return tableName(atributoBuscado.getType());
	}

	public static <T> Object getObjectFrom(Class<T> dtoClass, ResultSet rs, Connection con)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
			InstantiationException {

		Class<?> clazz = Class.forName(dtoClass.getName());
		Constructor<?> constructor = clazz.getConstructor();
		Object parentClassInstance = constructor.newInstance();
		T proxy = (T) Enhancer.create(dtoClass, new Handler(parentClassInstance, con));

		Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
			try {
				if (null != field.getAnnotation(Column.class)) {
					if (null != field.getType().getAnnotation(Table.class)) {
						Object classObject = getObjectFrom(field.getType(), rs, con);

						Field declaredField = parentClassInstance.getClass().getDeclaredField(field.getName());
						declaredField.setAccessible(Boolean.TRUE);
						declaredField.set(parentClassInstance, classObject);
					} else {
						Class<?> fieldClazz = Class.forName(field.getType().getName());
						Constructor<?> ctor = fieldClazz.getConstructor(String.class);
						Object object = ctor
								.newInstance(new Object[] { rs.getString(dtoClass.getAnnotation(Table.class).name()
										+ "." + field.getAnnotation(Column.class).name()) });

						Field declaredField = parentClassInstance.getClass().getDeclaredField(field.getName());
						declaredField.setAccessible(Boolean.TRUE);
						declaredField.set(parentClassInstance, object);
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

		return proxy;

	}

	/*
	 * Mï¿½todo muy ï¿½ltil recibe una clase A y devuelve un set ordenado con
	 * todas las clases con las que se relaciona esa clase A, incluyendo la
	 * misma clase A Ej: mï¿½tododo(Persona.class) devuelde Persona,
	 * Direcciï¿½n, Ocupaciï¿½n, etc
	 */
	public static <T> List<Class<?>> obtenerClasesDeRelaciones(Class<T> claseAEvaluar) {
		List<Class<?>> clasesEvaluadas = new ArrayList<>();
		List<Class<?>> clasesAEvaluar = new ArrayList<>();

		// Vamos a evaluar la clase actual asï¿½ que la aï¿½adimos en el set sin
		// repeticion
		clasesAEvaluar.add(claseAEvaluar);

		// Ahora llamamos al mï¿½todo que nos trae el set de clases y lo
		// retornamos
		clasesEvaluadas = obtenerClasesDeRelacionesRecursivo(claseAEvaluar, clasesEvaluadas, clasesAEvaluar);
		return clasesEvaluadas;
	}

	private static List<Class<?>> obtenerClasesDeRelacionesRecursivo(Class<?> claseActual,
			List<Class<?>> clasesEvaluadas, List<Class<?>> clasesAEvaluar) {
		Field[] variables = claseActual.getDeclaredFields();
		Class<?> claseDeLaVariable;

		// Buscamos todas las variables que sean de una tabla y las guardamos
		// para evaluar
		for (Field variable : variables) {
			claseDeLaVariable = variable.getType();

			if (!tableName(claseDeLaVariable).isEmpty() && !clasesEvaluadas.contains(claseDeLaVariable))

				clasesAEvaluar.add(claseDeLaVariable);
		}

		/*
		 * Ya obtuvimos todas las relaciones a otras tablas de esta clase asï¿½
		 * que las y la sacamos de las que se van a evaluar y de las evaluadas
		 */
		clasesAEvaluar.remove(claseActual);
		clasesEvaluadas.add(claseActual);

		// Llamada recursiva a este mï¿½todo si clasesAEvaluar aï¿½n contiene
		// elementos
		if (!clasesAEvaluar.isEmpty()) {// Si no estï¿½ vacï¿½o
			claseActual = clasesAEvaluar.get(0);// Obtenemos un elemento de las
												// que son a evaluar
			obtenerClasesDeRelacionesRecursivo(claseActual, clasesEvaluadas, clasesAEvaluar);
		}

		return clasesEvaluadas;
	}

	/*
	 * Se le pasa una clase y devuelve un HashMap con los nombres de los
	 * atributos en objetos y el nombre de los atributos de las tablas en
	 * paralelo. Tambiï¿½n se aï¿½dade el nombre de la clase y el nombre de la
	 * tabla Ej: Mï¿½todo(Persona) devuelve [<idPersona, id_persona>,
	 * <direccion, id_direccion>, <Persona, person>, etc]
	 */
	public static <T> Map<String, String> traducirDeObjetosARelacional(Class<T> clase) {
		Field[] variables = clase.getDeclaredFields();
		Map<String, String> nombreDelProgramadorYDeLaTabla = new HashMap<>();

		if (!tableName(clase).isEmpty())
			nombreDelProgramadorYDeLaTabla.put(clase.getSimpleName(), tableName(clase));

		for (Field variable : variables) {
			Optional<Column> posibleAnotacion = Optional.ofNullable(variable.getAnnotation(Column.class));

			if (posibleAnotacion.isPresent())
				nombreDelProgramadorYDeLaTabla.put(variable.getName(), posibleAnotacion.get().name());
		}

		return nombreDelProgramadorYDeLaTabla;
	}

	// Lo mismo que arriba pero para una lista de clases
	private static Map<String, String> traducirListaDeClases(List<Class<?>> clases) {
		Map<String, String> traduccion = new HashMap<>();

		for (Class<?> clase : clases)
			traduccion.putAll(traducirDeObjetosARelacional(clase));

		return traduccion;
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
				.replace("?primary_key", fields.get(PRIMARY_KEY)).replace("?statement", builder.toString());

		return transformedQuery;
	}

	/*
	 * Ejemplo sacado de página india INSERT INTO CUSTOMERS
	 * (ID,NAME,AGE,ADDRESS,SALARY) VALUES (1, 'Ramesh', 32, 'Ahmedabad',
	 * 2000.00 );
	 */
	public static String _insert(Object dto) {
		String insert = "INSERT INTO ";
		Map<String, String> atributosYValores = obtenerAtributosYValores(dto);
		traduccion = traducirDeObjetosARelacional(dto.getClass());
		List<String> atributos = new ArrayList<String>(atributosYValores.keySet());

		insert = insert + tableName(dto.getClass()) + " (";
		// Insert Into <Nombre de tabla> (
		
		//Añadir atributos
		for(String atributo:atributos){
			insert = insert + traduccion.get(atributo) + ",";
		}
		insert = insert.substring(0, insert.length()-1);
		// Insert Into <Nombre de tabla> (atributos
		
		insert = insert + ") VALUES (";
		
		//Añadir valores
		for(String atributo:atributos){
			insert = insert + atributosYValores.get(atributo) + ",";
		}
		insert = insert.substring(0, insert.length()-1);
		// Insert Into <Nombre de tabla> (atributos) Values (valores
		
		insert = insert + ");";
		return insert;
	}

	// Ejemplo de json
	// {"id":1,"tipo":"EBITDA","empresa":"Facebook","periodo":"2016","valor":10000}
	private static Map<String, String> obtenerAtributosYValores(Object dto){
		Map<String, String> atributosYValores = new HashMap<>();
		String json = new Gson().toJson(dto), valorActual;
		
		parsearJson(atributosYValores, json);
		
		for(String atributo:atributosYValores.keySet()){
			valorActual = atributosYValores.get(atributo);
			if(valorActual.contains("{"))
				atributosYValores.put(atributo, getFK(atributo, dto));
		}
		
		return atributosYValores;
	}

	private static String getFK(String atributo, Object dto) {
		Object objetoConFK = UBean.invokeGetter(dto, atributo);
		
		String atributoConID = Arrays.asList(objetoConFK.getClass().getDeclaredFields()).stream()
		.filter(field -> field.isAnnotationPresent(Id.class)).collect(Collectors.toList())
		.get(0).getName();
		
		Object fkFinal = UBean.invokeGetter(objetoConFK, atributoConID);
		
		return fkFinal.toString();
	}

	private static void parsearJson(Map<String, String> atributosYValores, String json) {
		String[] nombesDeCampos;
		json = sacarComillasYLLaves(json);
		nombesDeCampos = json.split(",");
		
		for(String campo:nombesDeCampos){
			String nombreDelAtributo = campo.split(":")[0];
			nombreDelAtributo = sacarComillasYLLaves(nombreDelAtributo);
			String valorDelAtributo = "";
			
			//Si el campo no es primitivo como persona o direccion
			//Selo guarda tal como está
			if(campo.contains("{"))
				atributosYValores.put(nombreDelAtributo,campo.substring(campo.indexOf('{')));
			else{
				valorDelAtributo = campo.split(":")[1];
				valorDelAtributo = reemplazarComillasPorComilla(valorDelAtributo);
				
				atributosYValores.put(nombreDelAtributo, valorDelAtributo);
			}
			
		}
	}
	
	//Si hay " las cambia por '
	private static String reemplazarComillasPorComilla(String stringConComillas) {
		if(stringConComillas.contains("\""))
			return "'" + sacarComillasYLLaves(stringConComillas) + "'";
		return stringConComillas;
	}
	
	//Saca las "" y las {} de los JSON
	private static String sacarComillasYLLaves(String stringSucio) {
		return stringSucio.substring(1, stringSucio.length()-1);
	}
}