package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.model.Direccion;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;

import java.lang.reflect.Method;

public class InterceptorTest {

    @Test
    public void intercept() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Direccion.class);

        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if (method.getName() == "getPersonas"){
                    System.out.println("Interceptando !!");
                }else {
                    System.out.println("No intercepto nada");
                }
                return null;
            }
        });

        Direccion direccion = (Direccion) enhancer.create();
        direccion.getPersonas();
    }

}
