package pl.khuzzuk.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;

class DAOManager {
    private SessionFactory factory;

    public DAOManager(SessionFactory factory) {
        this.factory = factory;
    }

    CriteriaBuilder getCriteriaBuilder() {
        return factory.getCriteriaBuilder();
    }

    synchronized Session openNewSession() {
        return factory.openSession();
    }

    void close() {
        factory.close();
    }
}
