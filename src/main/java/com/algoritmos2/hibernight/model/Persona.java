package com.algoritmos2.hibernight.model;

import com.algoritmos2.hibernight.model.annotations.Column;
import com.algoritmos2.hibernight.model.annotations.Id;
import com.algoritmos2.hibernight.model.annotations.Table;

@Table(name="persona")
public class Persona
{
	@Id(strategy=Id.IDENTITY)
	@Column(name="id_persona")
	private Integer idPersona;
	
	@Column(name="nombre")
	public String nombre;
		
	@Column(name="id_direccion")
	public Direccion direccion;
	
	@Column(name="id_ocupacion")
	public Ocupacion ocupacion;

	public Integer getIdPersona()
	{
		return idPersona;
	}

	public void setIdPersona(Integer idPersona)
	{
		this.idPersona=idPersona;
	}

	public String getNombre()
	{
		return nombre;
	}

	public void setNombre(String nombre)
	{
		this.nombre=nombre;
	}

	public Direccion getDireccion()
	{
		return direccion;
	}

	public void setDireccion(Direccion direccion)
	{
		this.direccion=direccion;
	}

	public Ocupacion getOcupacion()
	{
		return ocupacion;
	}

	public void setOcupacion(Ocupacion ocupacion)
	{
		this.ocupacion=ocupacion;
	}

	@Override
	public String toString()
	{
		return "Persona [idPersona="+idPersona+", nombre="+nombre+", direccion="+direccion+", ocupacion="+ocupacion+"]";
	}
	
	@Override
	public boolean equals(Object o)
	{
		Persona other = (Persona)o;
		boolean ok = true;
		ok = ok && idPersona==other.getIdPersona();
		ok = ok && nombre.equals(other.getNombre());

		if( direccion!=null )
		{
			ok = ok && direccion.getIdDireccion()==other.getDireccion().getIdDireccion();
		}
		else
		{
			ok = ok && other.getDireccion()==null;
		}
		
		if( ocupacion!=null )
		{
			ok = ok && ocupacion.getIdOcupacion()==other.getOcupacion().getIdOcupacion();
		}
		else
		{
			ok = ok && other.getOcupacion()==null;
		}
		
		return ok;
	}
	
	
	

	
}
