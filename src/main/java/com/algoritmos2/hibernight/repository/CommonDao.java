package com.algoritmos2.hibernight.repository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Map;

public class CommonDao {

    private SessionFactory sessionFactory;

    public CommonDao() {
    }


    @SuppressWarnings("unchecked")
    public <T> List<T> list(Class<T> clazz) {
        return sessionFactory.getCurrentSession().createCriteria(clazz).list();
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
