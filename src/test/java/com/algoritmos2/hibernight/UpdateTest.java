package com.algoritmos2.hibernight;

import com.algoritmos2.hibernight.config.DataBaseConfig;
import com.algoritmos2.hibernight.model.profeModel.Direccion;
import com.algoritmos2.hibernight.model.profeModel.Persona;
import com.algoritmos2.hibernight.repository.Query;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class UpdateTest extends DataBaseConfig {

    private Connection connection = getConnection();

    @Test
    public void _update() {
        String query = Query._update(Persona.class, "set $nombre=? where $nombre=?");

        assertEquals("UPDATE persona SET nombre=? WHERE nombre=?", query);
    }

    @Test
    public void update() throws SQLException {
        // query = Query.update(connection, Direccion.class, "set $numero=? where $numero=?", "11", "2331");

    }

}