package com.algoritmos2.hibernight.profe;

import java.sql.Connection;
import java.util.List;

import com.algoritmos2.hibernight.config.DataBaseConfig;
import com.algoritmos2.hibernight.model.profeModel.*;
import com.algoritmos2.hibernight.repository.Query;
import org.junit.Assert;
import org.junit.Test;


public class FindTest extends DataBaseConfig {

    @Test
    public void testFind()
    {
        Connection con = getConnection();

        // verifico el find
        Persona p = Query.find(con,Persona.class,12);
        Assert.assertEquals(p.getNombre(),"Pablo");
        Assert.assertEquals((Integer)p.getOcupacion().getIdOcupacion(),(Integer)4);

        // ocupacion es LAZY => debe permanecer NULL hasta que haga el get
        Assert.assertNull(p.ocupacion);

        // debe traer el objeto
        Ocupacion o = p.getOcupacion();
        Assert.assertNotNull(o);

        // verifico que lo haya traido bien
        Assert.assertEquals(o.getDescripcion(),"Ingeniero");

        // tipoOcupacion (por default) es EAGER => no debe ser null
        Assert.assertNotNull(o.getTipoOcupacion());
        TipoOcupacion to = o.getTipoOcupacion();

        // verifico que venga bien...
        Assert.assertEquals(to.getDescripcion(),"Profesional");

        // -- Relation --

        // las relaciones son LAZY si o si!
        Assert.assertNull(p.direcciones);

        List<PersonaDireccion> dirs = p.getDirecciones();
        Assert.assertNotNull(dirs);

        // debe tener 2 elementos
        Assert.assertEquals(dirs.size(),2);

        for(PersonaDireccion pd:dirs)
        {
            Persona p1 = pd.getPersona();
            Direccion d = pd.getDireccion();

            Assert.assertNotNull(p1);
            Assert.assertNotNull(d);

            Assert.assertEquals(p1.getNombre(),p.getNombre());
        }

    }
}
