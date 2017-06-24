package com.algoritmos2.hibernight.model.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

import com.algoritmos2.hibernight.model.annotations.Relation;
import com.algoritmos2.hibernight.repository.Query;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class Handler implements MethodInterceptor
{
	Object object;
	Connection con;

	public Handler(Object object, Connection con)
	{
		this.object = object;
		this.con = con;
		
	}

	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable
	{	
		if(UBean.isGetter(method.getName())){
			
		String atributo = UBean.getAttName(method.getName());
		Field field = object.getClass().getDeclaredField(atributo); 
		
		if ( (null != field.getAnnotation(Relation.class))){
			
			String att = field.getAnnotation(Relation.class).att();
	        String id = "id"+Character.toUpperCase(att.charAt(0))+ att.substring(1);
			String xql = "$ " + field.getAnnotation(Relation.class).att() + " =?"; 			
			List<?> list = Query.query(con,field.getAnnotation(Relation.class).type(),xql,UBean.invokeGetter(object,id));
			UBean.invokeSetter(object,atributo,list);
		}
		}
		
		return method.invoke(object,args);
		
	}

}
