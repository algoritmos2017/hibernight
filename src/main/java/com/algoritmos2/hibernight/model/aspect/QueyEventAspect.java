package com.algoritmos2.hibernight.model.aspect;

import com.algoritmos2.hibernight.model.Direccion;
import com.algoritmos2.hibernight.model.Persona;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;
import java.util.Collection;

@Aspect
public class QueyEventAspect {

    @Around("execution(@com.algoritmos2.hibernight.model.aspect.EnableQueryEventAspect * *(..)) && @annotation(enableQueryEventAspect)")
    public Collection<Persona> queyEvent(ProceedingJoinPoint pjp, EnableQueryEventAspect enableQueryEventAspect) throws Throwable {
        System.out.println("LAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return null;
    }

}
