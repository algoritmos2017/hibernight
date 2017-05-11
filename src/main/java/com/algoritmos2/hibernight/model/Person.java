package com.algoritmos2.hibernight.model;

import com.algoritmos2.hibernight.model.annotations.*;

@Table(name = "Person")
public class Person {

    @Id(strategy = Id.ASSIGNED)
    @Column(name = "id")
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "cellphone")
    String cellphone;

    Long numero;

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }
}
