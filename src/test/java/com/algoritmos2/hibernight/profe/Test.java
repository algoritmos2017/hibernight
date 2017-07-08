package com.algoritmos2.hibernight.profe;



import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;

import com.algoritmos2.hibernight.config.DataBaseConfig;
import com.algoritmos2.hibernight.model.profeModel.Direccion;
import com.algoritmos2.hibernight.model.profeModel.Ocupacion;
import com.algoritmos2.hibernight.model.profeModel.Persona;
import com.algoritmos2.hibernight.model.profeModel.PersonaDireccion;
import com.algoritmos2.hibernight.model.profeModel.TipoOcupacion;
import com.algoritmos2.hibernight.repository.Query;


public class Test extends DataBaseConfig 
{
	@org.junit.Test
	public void testFind() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException, SecurityException, SQLException, NoSuchFieldException
	{
		Connection con = getConnection();
		
		// verifico el find
		Persona p = Query.find(con,Persona.class,13);
		Assert.assertEquals(p.getNombre(),"Hernan");
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
		Assert.assertEquals(dirs.size(),1);
		
		for(PersonaDireccion pd:dirs)
		{
			Persona p1 = pd.getPersona();
			Direccion d = pd.getDireccion();
			
			Assert.assertNotNull(p1);
			Assert.assertNotNull(d);
		
			Assert.assertEquals(p1.getNombre(),p.getNombre());
		}
	}
	
	@org.junit.Test
	public void testXQL() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException, SQLException, NoSuchFieldException, SecurityException
	{
		Connection con = getConnection();

		String xql="$ocupacion.tipoOcupacion.descripcion LIKE ?";
		List<Persona> lst =Query.query(con,Persona.class,xql,"Profesional");
		
		Assert.assertEquals(lst.size(),5);

		for(Persona p:lst)
		{
			Assert.assertEquals(p.getOcupacion().getTipoOcupacion().getDescripcion(),"Profesional");
		}
	}
}
