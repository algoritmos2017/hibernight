package com.algoritmos2.hibernight.model.mapper;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;

public class UBean
{
    // invoca el getter correspondiente al atributo attName sobre el objeto tgt
    public static Object invokeGetter(Object tgt, String attName)
    {
        try
        {
            // obtengo el nombre del getter para el atributo attName
            String getterName = getGetterName(attName);
            
            // obtengo un puntero al metodo de acceso; el segundo argumento (null) indica que,
            // de estar sobrecargado, me interesa la version del metodo que no recibe argumentos
            Method mtd = tgt.getClass().getMethod(getterName, null);
            
            // invoco al getter si pasarle ningun argumento
            return mtd.invoke(tgt, null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // invoca el setter correspondiente al atributo attName sobre el objeto tgt asignando el valor value
    public static void invokeSetter(Object tgt, String attName, Object value)
    {
        try
        {
            // obtengo un puntero al atributo
            Field f = tgt.getClass().getDeclaredField(attName);
            
            // obtengo el nombre de su setter
            String setterName = getSetterName(attName);
            
            // obtengo un puntero al setter; de estar sobrecargado me interesa 
            // la version del metodo que recibe un unico argumento cuyo tipo coincide con el del atributo f
            Method mtd = tgt.getClass().getMethod(setterName, f.getType());
            
            // invoco al setter pasandole el objeto value (que recibo como parametro)
            mtd.invoke(tgt, value);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    // retorna la lista de atributos que define la clase c    
    public static String[] getAttNames(Class<?> c)
    {
        Vector<String> getters = new Vector<>();
        Vector<String> setters = new Vector<>();
        
        // obtengo todos los metodos que declara la clase c
        Method[] mtds = c.getDeclaredMethods();
        
        for(Method mtd:mtds)
        {
            // los setters los meto en un vector
            if( mtd.getName().startsWith("set") )
            {
                setters.add(mtd.getName());
            }
            else
            {
                // los getters en otro vector separado
                if( mtd.getName().startsWith("get"))
                {
                    getters.add(mtd.getName());
                }
            }
        }

        // recorro los getters y para cada uno busco que exista un setters relacionado        
        Vector<String> attNames = new Vector<>();
        for(String getter: getters)
        {
            String aux = getSetterName(getAttName(getter));
            if( setters.contains(aux) )
            {
                // si para este getter existe un setter entonces: ES UN ATRIBUTO!
                attNames.add( getAttName(getter));
            }
        }
        
        String ret[] = new String[attNames.size()];
        for(int i=0; i<ret.length; i++)
        {
            ret[i]=attNames.get(i);
        }
        
        return ret;
    }
    
    // dado el nombre de un atributo, retorna el nombre de su getter
    public static String getGetterName(String attName)
    {
        return "get" + Character.toUpperCase(attName.charAt(0)) + attName.substring(1);
    }

    // dado el nombre de un atributo, retorna el nombre de su setter
    public static String getSetterName(String attName)
    {
        return "set" + Character.toUpperCase(attName.charAt(0)) + attName.substring(1);
    }
    
    // dado el nombre de un setter o un getter, retorna el nombre del atributo relacionado
    public static String getAttName(String accessorMethod)
    {
        String aux = accessorMethod.substring(3);
        return Character.toLowerCase(aux.charAt(0))+ aux.substring(1);
    }    
    
    // dado un string, retorna si es getter
    public static Boolean isGetter(String accessorMethod)
    {
       return (accessorMethod.startsWith("get") && Character.isUpperCase(accessorMethod.charAt(3)));	
    }    
}