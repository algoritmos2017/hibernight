package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.model.Direccion;
import org.junit.Test;

public class AspectTest {

    @Test
    public void direccionAspectTest(){
        Direccion direccion = new Direccion();
        direccion.getPersonas();
    }

}
