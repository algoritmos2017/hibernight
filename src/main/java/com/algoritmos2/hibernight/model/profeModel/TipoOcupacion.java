package com.algoritmos2.hibernight.model.profeModel;

import com.algoritmos2.hibernight.model.annotations.Column;
import com.algoritmos2.hibernight.model.annotations.Id;
import com.algoritmos2.hibernight.model.annotations.Table;

@Table(name = "tipo_ocupacion")
public class TipoOcupacion {

    @Id(strategy = Id.IDENTITY)
    @Column(name = "id_tipoocupacion")
    private Integer idTipoOcupacion;

    @Column(name = "descripcion")
    private String descripcion;

    public Integer getIdTipoOcupacion() {
        return idTipoOcupacion;
    }

    public void setIdTipoOcupacion(Integer idTipoOcupacion) {
        this.idTipoOcupacion = idTipoOcupacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return getDescripcion();
    }

    @Override
    public boolean equals(Object obj) {
        TipoOcupacion other = (TipoOcupacion) obj;
        return other.getIdTipoOcupacion().equals(getIdTipoOcupacion())
                && other.getDescripcion().equals(getDescripcion());
    }


}
