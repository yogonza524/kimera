/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.controller;

import com.core.enums.OrderBy;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Class to implements the controller to get DataBase entities
 * @author Gonza
 */
public class Kimera implements Serializable{
    
    /**
     *
     */
    protected static SessionFactory sf;

    public Kimera(SessionFactory sessionFactory) {
        Kimera.sf = sessionFactory;
    }
    
    /**
     * List all object in a table
     * @param <T> Generic type Result
     * @param type
     * @return List of entity object for table T. Object receiver must be the type List<type>
     */
    public <T> T all(Class type){
        Session s = sf.openSession();
        List<T> l = s.createCriteria(type).list();
        for(Object o : l){
            o = initialize(o);
        }
        s.close();
        return (T) l;
    }
    
    /**
     * Get an object from DataBase by key Id
     * @param key represents the id for object entity. If the entity is complex then the id must be like "id.id" for example.
     * @param id represents the id for the table. Can be an Integer, String, etc. Depends the Table design.
     * @return Entity object width the fields initialized with Hibernate
     */
    public <T> T entityById(String key,Object id, Class type){
        Session s = sf.openSession();
        T result = (T)s.createCriteria(type).add(Restrictions.eq(key, id)).setMaxResults(1).uniqueResult();
        Hibernate.initialize(result);
        s.close();
        //result = this.initialize(result);
        return result;
    }
    /**
     * Delete obj in DataBase
     * @param obj
     * @return true if obj was removed successfuly. False in otherwise.
     */
    public boolean remove(Object obj){
        boolean resp = false;
        try {
            Session s = sf.openSession();
            Transaction tx = s.beginTransaction();
            s.delete(obj);
            tx.commit();
            s.close();
            resp = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }
    /**
     * Persist obj in DataBase
     * @param obj
     * @return true if obj was added suyccessfuly- False in otherwise.
     */
    public boolean add(Object obj){
        boolean resp = false;
        try {
            Session s = sf.openSession();
            Transaction tx = s.beginTransaction();
            s.persist(obj);
            tx.commit();
            s.close();
            resp = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }
    
    /**
     * Update obj in DataBase
     * @param obj
     * @return true if updated obj successfuly, false in otherwise
     */
    public boolean update(Object obj){
        boolean resp = false;
        try {
            Session s = sf.openSession();
            Transaction tx = s.beginTransaction();
//            s.clear();
            s.update(obj);
            tx.commit();
            s.close();
            resp = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }
    
    /**
     * Gets the last object of the table , if there initializes its attributes of entity type . But returns null .
     * @param <T>
     * @param key
     * @param type
     * @return Last object with the key param if exists. Otherwise returns null.
     */
    public <T> T getLast(String key, Class type){
        T result = null;
        try {
            Session s = sf.openSession();
            result = (T) s.createCriteria(type).addOrder(Order.desc(key)).setMaxResults(1).uniqueResult();
            Hibernate.initialize(result);
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }     
        
    public <T> T entityByRestrictions(List<Criterion> restrictions, Class type){
        Session s = sf.openSession();
        Criteria cri = s.createCriteria(type);
        Iterator i = restrictions.iterator();
        while(i.hasNext()){
            Criterion c = (Criterion)i.next();
            cri.add(c);
        }
        T result = (T) cri.setMaxResults(1).uniqueResult();
        Hibernate.initialize(result);
        s.close();
        return result;
    }
    
    public <T> T listByRestrictions(List<Criterion> restrictions, Class type){
        Session s = sf.openSession();
        Criteria cri = s.createCriteria(type);
        Iterator i = restrictions.iterator();
        while(i.hasNext()){
            Criterion c = (Criterion)i.next();
            cri.add(c);
        }
        List<T> result = (List<T>) cri.list();
        Hibernate.initialize(result);
        s.close();
        return (T) result;
    }
    
    /**
     * Hibernate Initialization for the entity
     * @param entity
     * @return the same object but if any of their attributes has the notation "Entity " that attribute will be initialized by Hibernate
     */
    public <T> T initialize(Object entity){
            if(entity != null){
                Hibernate.initialize(entity);
                for(Field f : entity.getClass().getDeclaredFields()){
                        Class type = f.getType();
                        Annotation[] notes = type.getAnnotations();
                        for(Annotation n : notes){
                                if(n.toString().contains("Entity")){
                                        Hibernate.initialize(f);
                                }
                        }
                }
            }
            return (T) entity;
    }
    
    public <T> T listWithRestrictions(Map<String,Object> restrictions, Class type){
        Session s = sf.openSession();
        Criteria cri = s.createCriteria(type);
        for(Map.Entry<String,Object> entry : restrictions.entrySet()){
            cri.add(Restrictions.eq(entry.getKey(), entry.getValue()));
        }
        List<T> output = cri.list();
        Hibernate.initialize(output);
        s.close();
        return (T) output;
    }
    
    public <T> T entityWithParams(Map<String,Object> restrictions,Class type){
        Session s = sf.openSession();
        Criteria cri = s.createCriteria(type);
        for(Map.Entry<String,Object> entry : restrictions.entrySet()){
            cri.add(Restrictions.eq(entry.getKey(), entry.getValue()));
        }
        T output = (T) cri.setMaxResults(1).uniqueResult();
        Hibernate.initialize(output);
        s.close();
        return output;
    }
    
    public <T> T entityWithRestrictionsLike(Map<String,Object> restrictions, Class type){
        Session s = sf.openSession();
        Criteria cri = s.createCriteria(type);
        for(Map.Entry<String,Object> entry : restrictions.entrySet()){
            cri.add(Restrictions.like(entry.getKey(),"%" + entry.getValue() + "%"));
        }
        List<T> output = cri.list();
        Hibernate.initialize(output);
        s.close();
        return (T) output;
    }
    
    public <T> T entityWithOneRestriction(String key, Object value, Class type){
        Session s = sf.openSession();
        Criteria cri = s.createCriteria(type);
        cri.add(Restrictions.eq(key, value));
        List<T> output = cri.list();
        Hibernate.initialize(output);
        s.close();
        return (T) output;
    }
    
    public <T> T entityByIdLike(String key, String value, Class type){
        
        Session s = sf.openSession();
        Criteria cri = s.createCriteria(type);
        cri.add(Restrictions.like(key,"%" + value + "%"));
        List<T> output = cri.list();
        Hibernate.initialize(output);
        s.close();
        return (T) output;
    }
    
    public <T> T listOrderBy(String key, OrderBy order, Class type){
        Session s = sf.openSession();
        Criteria c = s.createCriteria(type);
        c.addOrder(order.equals(OrderBy.ASC)? Order.asc(key): Order.desc(key));
        List<T> output = c.list();
        Hibernate.initialize(output);
        s.close();
        return (T) output;
    }
    
    public <T> T entityWithRestrictionsOrderBy(Map<String,Object> restrictions,String key, OrderBy order, Class type){
        Session s = sf.openSession();
        Criteria cri = s.createCriteria(type);
        for(Map.Entry<String,Object> entry : restrictions.entrySet()){
            cri.add(Restrictions.eq(entry.getKey(), entry.getValue()));
        }
        cri.addOrder(order.equals(OrderBy.ASC)? Order.asc(key): Order.desc(key));
        List<T> output = cri.list();
        Hibernate.initialize(output);
        s.close();
        return (T) output;
    }
    
    public <T> T entityWithRestrictions(List<Criterion> restrictions, Class type){
        Session s = sf.openSession();
        Criteria cri = s.createCriteria(type);
        Iterator i = restrictions.iterator();
        while(i.hasNext()){
            Criterion c = (Criterion)i.next();
            cri.add(c);
        }
        List<T> output = cri.list();
        Hibernate.initialize(output);
        s.close();
        return (T) output;
    }
    
    public List callProcedure(String query, Map<String,Object> params){
        List result = null;
        try {
            Session s = sf.openSession();
            Transaction tx = s.beginTransaction();
            Query consulta = s.createSQLQuery(query);
                if (params != null) {
                    Iterator it = params.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        consulta.setParameter((String)pair.getKey(), pair.getValue());
                    }
                }
                result = consulta.list();
                tx.commit();
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
        }
        return result;
    }
    
    public <T> T entityBetweenDates(String field, Date first, Date second, Class type){
        List<T> result = null;
        try {
            Session s = sf.openSession();
            result = s.createCriteria(type).add(Restrictions.between(field, first, second)).list();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) result;
    }
}
