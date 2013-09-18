/**
 * Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
 *
 * MEDICAL RESEARCH COUNCIL UK MRC
 *
 * Harwell Mammalian Genetics Unit
 *
 * http://www.har.mrc.ac.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mousephenotype.dcc.utils.persistence;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.ejb.EntityManagerFactoryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class HibernateManager {

    private static final Logger logger = LoggerFactory.getLogger(HibernateManager.class);
    public static final String PERSISTENCEUNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report:org.mousephenotype.dcc.exportlibrary.datastructure.core.common";
    private EntityManagerFactory entityManagerFactory;
    private EntityManagerFactoryImpl entityManagerFactoryImp;
    private EntityManager entityManager;
    private Session session;
    private final String _persistenceUnitName;

    private void setup(Map properties, String persistenceUnitname) throws HibernateException {
        logger.trace("persistence properties");
        for (Object key : properties.keySet()) {
            logger.trace("{} {}", key, properties.get(key));
        }

        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitname == null ? PERSISTENCEUNITNAME : persistenceUnitname, properties);
        entityManagerFactoryImp = (EntityManagerFactoryImpl) entityManagerFactory;
    }

    public HibernateManager(Properties properties, String persistenceUnitname) throws HibernateException {
        this._persistenceUnitName = persistenceUnitname;
        this.setup(properties, persistenceUnitname);
    }

    public String getPersistencename() {
        return this._persistenceUnitName;
    }

    public EntityManager getEntityManager() {
        if (this.entityManager == null || !this.entityManager.isOpen()) {
            entityManager = entityManagerFactoryImp.createEntityManager();
        }
        return entityManager;
    }

    public EntityManagerFactoryImpl getEntityManagerFactoryImpl() {
        return this.entityManagerFactoryImp;
    }

    public synchronized void close() throws HibernateException {

        if (this.entityManager != null && this.entityManager.isOpen()) {
            this.entityManager.close();
        }
        if (this.session != null && this.session.isOpen()) {
            if (this.session.isDirty()) {
                this.session.flush();
            }
            this.session.close();
        }
        if (this.entityManagerFactoryImp != null && this.entityManagerFactoryImp.isOpen()) {
            this.entityManagerFactoryImp.close();
        }

        if (this.entityManagerFactory != null && this.entityManagerFactory.isOpen()) {

            this.entityManagerFactory.close();
        }
    }

    public  <T> List<T> query(String query) throws HibernateException {
        this.refreshSession();
        List<T> results = this.session.createQuery(query).list();
        return results;
    }
    
    

    public  <T, U> T getContainer(U continent, Class<T> containersClazz, String containerAttribute) {

        //String lexicalQuery="from Submission submission inner join fetch submission.centreProcedure centreProcedure  where centreProcedure = :selectedCentreProcedure";
        StringBuilder lexicalQuery = new StringBuilder(" from ");

        lexicalQuery.append(containersClazz.getName());
        lexicalQuery.append(" continent ");
        lexicalQuery.append(" inner join fetch ");
        lexicalQuery.append("continent.");
        lexicalQuery.append(containerAttribute);
        lexicalQuery.append(" ");
        lexicalQuery.append(containerAttribute);
        lexicalQuery.append(" where ");
        lexicalQuery.append(containerAttribute);
        lexicalQuery.append(" = :continent");

        this.refreshSession();

        org.hibernate.Query query = this.session.createQuery(lexicalQuery.toString());
        query.setParameter("continent", continent);

        return (T) query.uniqueResult();
    }

    /*
     * http://docs.jboss.org/hibernate/orm/4.0/hem/en-US/html/querycriteria.html
     */
    public synchronized CriteriaBuilder getCriteriaBuilder() {
        return this.entityManagerFactoryImp.getCriteriaBuilder();
    }

    public <T> CriteriaQuery<T> getCriteriaQuery(Class<T> clazz) {
        return this.getCriteriaBuilder().createQuery(clazz);
    }

    public synchronized <T> List<T> executeCriteriaQuery(CriteriaQuery<T> criteriaQuery) throws IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, RuntimeException {
        this.refreshEntityManager();
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public synchronized <T> void persist(T object) throws IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, RuntimeException {
        EntityTransaction entityTransaction = null;
        this.refreshEntityManager();
        try {
            entityTransaction = this.entityManager.getTransaction();
            entityTransaction.begin();
            this.entityManager.persist(object);
            entityTransaction.commit();
        } catch (RuntimeException ex) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw ex; // or display error message
        }
    }

    public synchronized <T> T merge(T object) throws IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, RuntimeException {
        EntityTransaction entityTransaction = null;
        T toReturn = null;
        this.refreshEntityManager();
        try {
            entityTransaction = this.entityManager.getTransaction();
            entityTransaction.begin();
            toReturn = this.entityManager.merge(object);
            entityTransaction.commit();
        } catch (RuntimeException ex) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw ex; // or display error message
        }
        return toReturn;
    }

    public synchronized <T> List<T> merge(List<T> objects) throws IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, RuntimeException {
        EntityTransaction entityTransaction = null;
        List<T> merged = new ArrayList<T>();
        this.refreshEntityManager();
        try {
            entityTransaction = this.entityManager.getTransaction();
            entityTransaction.begin();
            for (T object : objects) {
                this.entityManager.persist(object);
            }
            entityTransaction.commit();
        } catch (RuntimeException ex) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                logger.error("rolling back transaction {}", ex);
                entityTransaction.rollback();
            }
            throw ex; // or display error message
        }
        return merged;
    }

    public synchronized <T> T load(Class<T> clazz, long hjid) throws IllegalArgumentException {
        this.refreshEntityManager();
        logger.info("find {} with hjid", clazz.getName(), hjid);

        return this.entityManager.find(clazz, hjid);
    }

    public synchronized <T> List<T> query(String query, Class<T> clazz) throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException,
            LockTimeoutException, PersistenceException {
        this.refreshEntityManager();

        return this.entityManager.createQuery(query, clazz).getResultList();
    }

    public synchronized <T> T uniqueResult(Class<T> clazz) throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException,
            LockTimeoutException, PersistenceException, NoResultException {
        this.refreshEntityManager();

        return this.entityManager.createQuery("from " + clazz.getName(), clazz).getSingleResult();
    }

//ImmutableMap.<String, Object>builder().put("STRAINID", strainID).build()
    public <T> List<T> query(String query, Map<String, Object> parameters, Class<T> clazz) throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException,
            LockTimeoutException, PersistenceException {
        this.refreshEntityManager();
        TypedQuery<T> createQuery = this.entityManager.createQuery(query, clazz);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            createQuery.setParameter(entry.getKey(), entry.getValue());
        }

        return createQuery.getResultList();
    }

    /**
     * Synchronize the persistence context to the underlying database.
     *
     * @throws TransactionRequiredException if there is no transaction
     * @throws PersistenceException if the flush fails
     *
     * First modify the field values of any of your mapped objects Then invoke
     * alter
     */
    public synchronized void alter() throws TransactionRequiredException, PersistenceException {
        this.refreshEntityManager();
        this.entityManager.getTransaction().begin();
        this.entityManager.flush();
        //this.entityManager.clear();
        this.entityManager.getTransaction().commit();
    }

    /**
     * Remove the given entity from the persistence context, causing a managed
     * entity to become detached. Unflushed changes made to the entity if any
     * (including removal of the entity), will not be synchronized to the
     * database. Entities which previously referenced the detached entity will
     * continue to reference it.
     *
     * @param entity entity instance
     * @throws IllegalArgumentException if the instance is not an entity
     * @since Java Persistence 2.0
     */
    public synchronized <T> void detach(T object) {
        this.refreshEntityManager();
        this.entityManager.detach(object);
    }

    public int execution(String update) {
        this.refreshSession();
        Transaction transaction = null;
        int result = -1;
        try {
            transaction = session.beginTransaction();
            org.hibernate.Query query = session.createQuery(update);
            result = query.executeUpdate();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                logger.error("rolling back transaction {}", ex);
                transaction.rollback();
            }
        }
        return result;
    }

    public int nativeExecution(String update) {
        this.refreshSession();
        Transaction transaction = null;
        int result = -1;
        try {
            transaction = session.beginTransaction();
            SQLQuery sqlQuery = session.createSQLQuery(update);
            result = sqlQuery.executeUpdate();
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                logger.error("rolling back transaction {}", ex);
                transaction.rollback();
            }
        }
        return result;
    }

    public int nativeExecution(String update, Map<String, Object> parameters) {
        this.refreshSession();
        Transaction transaction = null;
        int result = -1;
        try {
            transaction = session.beginTransaction();
            SQLQuery sqlQuery = session.createSQLQuery(update);
            if (parameters != null) {
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    sqlQuery.setParameter(entry.getKey(), entry.getValue());
                }
            }
            result = sqlQuery.executeUpdate();
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                logger.error("rolling back transaction {}", ex);
                transaction.rollback();
            }
        }
        return result;
    }

    public List nativeQuery(String query) {
        this.refreshSession();
        Transaction transaction = null;
        List result = null;
        try {
            transaction = session.beginTransaction();
            SQLQuery sqlQuery = session.createSQLQuery(query);
            result = sqlQuery.list();
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                logger.error("rolling back transaction {}", ex);
                transaction.rollback();
            }
        }
        return result;
    }

    public <T> List<T> nativeQuery(String query, Class<T> clazz) {
        this.refreshSession();
        Transaction transaction = null;
        List<T> result = null;
        try {
            transaction = session.beginTransaction();
            SQLQuery sqlQuery = session.createSQLQuery(query).addEntity(clazz);
            result = sqlQuery.list();
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                logger.error("rolling back transaction {}", ex);
                transaction.rollback();
            }
        }
        return result;
    }

    public <T> List<T> nativeQuery(String query, Class<T> clazz, com.google.common.collect.Table<String, Class, Object> parameters) {
        this.refreshSession();
        Transaction transaction = null;
        List<T> result = null;
        try {
            transaction = session.beginTransaction();
            SQLQuery sqlQuery = session.createSQLQuery(query).addEntity(clazz);
            if (parameters != null) {
                for (com.google.common.collect.Table.Cell<String, Class, Object> entry : parameters.cellSet()) {
                    if (entry.getColumnKey().equals(String.class)) {
                        sqlQuery.setString(entry.getRowKey(), (String) entry.getValue());
                        continue;
                    }
                    if (entry.getColumnKey().equals(Long.class)) {
                        sqlQuery.setLong(entry.getRowKey(), (Long) entry.getValue());
                        continue;
                    }
                    if (entry.getColumnKey().equals(BigInteger.class)) {
                        sqlQuery.setBigInteger(entry.getRowKey(), (BigInteger) entry.getValue());
                        continue;
                    }
                    logger.error("Type {} not registered", entry.getValue().getClass());
                }
            }

            result = sqlQuery.list();
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                logger.error("rolling back transaction {}", ex);
                transaction.rollback();
            }
        }
        return result;
    }

    public List nativeQuery(String query, Map<String, org.hibernate.type.Type> scalars) {

        this.refreshSession();
        Transaction transaction = null;
        List result = null;
        try {
            transaction = session.beginTransaction();
            SQLQuery sqlQuery = session.createSQLQuery(query);
            for (Map.Entry<String, org.hibernate.type.Type> entry : scalars.entrySet()) {
                sqlQuery.addScalar(entry.getKey(), entry.getValue());
            }
            result = sqlQuery.list();
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                logger.error("rolling back transaction {}", ex);
                transaction.rollback();
            }
        }
        return result;
    }

    //("imitsID",Long.class,331L);name type value
    public List nativeQuery(String query, Map<String, org.hibernate.type.Type> scalars, com.google.common.collect.Table<String, Class, Object> parameters) {

        this.refreshSession();
        Transaction transaction = null;
        List result = null;
        try {
            transaction = session.beginTransaction();
            SQLQuery sqlQuery = session.createSQLQuery(query);
            if (scalars != null) {
                for (Map.Entry<String, org.hibernate.type.Type> entry : scalars.entrySet()) {
                    sqlQuery.addScalar(entry.getKey(), entry.getValue());
                }
            }
            if (parameters != null) {
                for (com.google.common.collect.Table.Cell<String, Class, Object> entry : parameters.cellSet()) {
                    if (entry.getColumnKey().equals(String.class)) {
                        sqlQuery.setString(entry.getRowKey(), (String) entry.getValue());
                        continue;
                    }
                    if (entry.getColumnKey().equals(Long.class)) {
                        sqlQuery.setLong(entry.getRowKey(), (Long) entry.getValue());
                        continue;
                    }
                    if (entry.getColumnKey().equals(BigInteger.class)) {
                        sqlQuery.setBigInteger(entry.getRowKey(), (BigInteger) entry.getValue());
                        continue;
                    }
                    logger.error("Type {} not registered", entry.getValue().getClass());
                }
            }


            result = sqlQuery.list();
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                logger.error("rolling back transaction {}", ex);
                transaction.rollback();
            }
        }
        return result;
    }

    public List aggregateResults(String lexicalQuery) throws HibernateException {
        this.refreshSession();
        org.hibernate.Query query = session.createQuery(lexicalQuery);
        return query.list();
    }

    /*
     * add a query over a session method with parameters, not over entitymanager
     *
     */
    public BigInteger count(String lexicalQuery, Map<String, Object> parameters) throws HibernateException {
        this.refreshSession();
        org.hibernate.Query query = session.createQuery(lexicalQuery);
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return BigInteger.valueOf((Long) query.uniqueResult());
    }

    public <T> T uniqueResult(String lexicalQuery, Map<String, Object> parameters) throws HibernateException {
        this.refreshSession();
        org.hibernate.Query query = session.createQuery(lexicalQuery);
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return (T) query.uniqueResult();
    }

    public List aggregateResults(String lexicalQuery, Map<String, Object> parameters) throws HibernateException {
        this.refreshSession();
        org.hibernate.Query query = session.createQuery(lexicalQuery);
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return query.list();
    }

    public void remove(Object object) {
        this.refreshEntityManager();
        //if (this.entityManager != null && this.entityManager.isOpen()) {
        if (this.entityManager.contains(object)) {
            this.entityManager.getTransaction().begin();
            this.entityManager.remove(object);
            this.entityManager.getTransaction().commit();
            logger.info("object has been removed successfully");
        } else {
            logger.info("object not found");
        }
        //}
    }

    //http://stackoverflow.com/questions/5482141/what-is-the-difference-between-entitymanager-find-and-entitymanger-getreferenc
    //if the object has already been loaded, find doen't need to connect to the database.
    public <T> void remove(Class<T> clazz, Serializable id) {
        this.refreshEntityManager();
        Object toBeRemoved = null;
        //if (this.entityManager != null && this.entityManager.isOpen()) {
        toBeRemoved = this.entityManager.find(clazz, id);
        if (toBeRemoved != null) {
            this.entityManager.getTransaction().begin();
            this.entityManager.remove(toBeRemoved);
            this.entityManager.getTransaction().commit();
            logger.info("entity id {} has been removed", id);
        } else {

            logger.error("object with id {} was not present", id);
        }
        //}
    }

    private void refreshSession() {
        if (this.session == null || !this.session.isOpen()) {
            this.session = entityManagerFactoryImp.getSessionFactory().openSession();
        }
    }

    private void refreshEntityManager() {
        if (this.entityManager == null || !this.entityManager.isOpen()) {
            entityManager = entityManagerFactoryImp.createEntityManager();
        }
    }
}
