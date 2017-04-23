package com.algoritmos2.hibernight.model;

import com.algoritmos2.hibernight.model.annotations.*;

@Table(name = "Person")
public class Person {

    @Id(strategy = Id.ASSIGNED)
    @Column(name = "person_name")
    String name;

    @Column(name = "cellphone")
    String cellphone;

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
}
