package com.algoritmos2.hibernight.model;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {

    List<String> columns;
    String tablaName;
    List<String> joins;
    String where;

    public QueryBuilder() {
        this.columns = new ArrayList<String>();
        this.joins = new ArrayList<String>();
    }

    public List<String> getColumns() {
        return columns;
    }

    public void addColumn(String moreColumns) {
        this.columns.add(moreColumns);
    }

    public void addJoin(String join){
        this.joins.add(join);
    }

    public String getTablaName() {
        return tablaName;
    }

    public void setTablaName(String tablaName) {
        this.tablaName = tablaName;
    }

    public List<String> getJoins() {
        return joins;
    }

    public void setJoins(List<String> joins) {
        this.joins = joins;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
