package com.algoritmos2.hibernight.model;


import com.algoritmos2.hibernight.model.annotations.*;

import java.util.List;

@Table(name="dept", alias="d")
public class Dept 
{
	@Id(strategy=Id.ASSIGNED)
	@Column(name="deptno")
	@Gui(editable=true)
	private Integer deptno;
	
	@Column(name="dname")
	private String dname;
	
	@Column(name="loc")
	private String loc;
	
	@Relation(type=Emp.class, att="dept")
	private List<Emp> emps;
	
	public Integer getDeptno()
	{ 
		return deptno;
	}

	public void setDeptno(Integer deptno)
	{
		this.deptno=deptno;
	}

	public String getDname()
	{
		return dname;
	}

	public void setDname(String dname)
	{
		this.dname=dname;
	}

	public String getLoc()
	{
		return loc;
	}

	public void setLoc(String loc)
	{
		this.loc=loc;
	}

	@Override
	public String toString()
	{
		return getDname();
	}

	public List<Emp> getEmps()
	{
		return emps;
	}

	public void setEmps(List<Emp> emps)
	{
		this.emps = emps;
	}	
	
	public boolean equals(Object o)
	{
		return ((Dept)o).getDeptno()==getDeptno();
				
	}
}
