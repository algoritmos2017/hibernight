package com.algoritmos2.hibernight.repository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Map;

public class Connection {

    private SessionFactory sessionFactory;

    public Connection(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(String query) {
        //return sessionFactory.getCurrentSession().createCriteria(clazz).list();
        return sessionFactory.getCurrentSession().createSQLQuery(query).list();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, String query) {
        //return (T) sessionFactory.getCurrentSession().createCriteria(clazz).add().uniqueResult();
        return null;
    }

    public <T> T get(Class<T> clazz, Map<String, String> params) {
        return (T) sessionFactory.getCurrentSession().createCriteria(clazz).add(Restrictions.allEq(params)).uniqueResult();
    }
}
