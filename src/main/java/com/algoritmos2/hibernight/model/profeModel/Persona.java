package com.algoritmos2.hibernight.model.profeModel;

import com.algoritmos2.hibernight.model.annotations.Column;
import com.algoritmos2.hibernight.model.annotations.Id;
import com.algoritmos2.hibernight.model.annotations.Relation;
import com.algoritmos2.hibernight.model.annotations.Table;

import java.util.List;

@Table(name = "persona")
public class Persona {

    @Column(name = "id_ocupacion", fetchType = Column.LAZY)
    public Ocupacion ocupacion;

    @Relation(att = "persona", type = PersonaDireccion.class)
    public List<PersonaDireccion> direcciones;

    @Id(strategy = Id.IDENTITY)
    @Column(name = "id_persona")
    private Integer idPersona;

    @Column(name = "nombre")
    private String nombre;

    public List<PersonaDireccion> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<PersonaDireccion> direcciones) {
        this.direcciones = direcciones;
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Ocupacion getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(Ocupacion ocupacion) {
        this.ocupacion = ocupacion;
    }

    @Override
    public boolean equals(Object o) {
        Persona other = (Persona) o;
        return other.getIdPersona().equals(getIdPersona())
                && other.getNombre().equals(getNombre())
                && other.getOcupacion().equals(getOcupacion());
    }

    public String toString() {
        return getNombre();
    }
}