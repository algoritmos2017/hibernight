package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.config.DataBaseConfig;
import com.algoritmos2.hibernight.model.Direccion;
import com.algoritmos2.hibernight.model.annotations.Relation;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public class InterceptorTest extends DataBaseConfig {

    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        connection = getConnection();
    }

    @After
    public void finalize() throws SQLException {
        closeConnection();
    }

    @Test
    public void intercept() throws NoSuchFieldException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Direccion.class);

        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if (null != method.getAnnotation(Relation.class)) {
                    System.out.println("Interceptando !!");
                } else {
                    System.out.println("No intercepto nada");
                }
                return null;
            }
        });
        Direccion direccion = (Direccion) enhancer.create();
        //Direccion direccion = new Direccion();
        direccion.getPersonas();
    }

}
