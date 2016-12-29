package pl.khuzzuk.dao;


import org.hibernate.cfg.Configuration;

import java.util.*;
import java.util.stream.Collectors;

public class DAO {
    private DAOManager manager;
    private Map<Class<?>, DAOTransactional<? extends Named<? extends Comparable>, ? extends Comparable>> resolvers;

    @SafeVarargs
    public final void initResolvers(Class<? extends Named<? extends Comparable<?>>>... classes) {
        if (Arrays.stream(classes).filter(this::isNotNamed).count() != 0) {
            throw new IllegalArgumentException("All classes should implement Named<U> interface");
        }
        Configuration configure = new Configuration().configure();
        Arrays.stream(classes).forEach(configure::addAnnotatedClass);
        manager = new DAOManager(configure.buildSessionFactory());
        resolvers = new HashMap<>();
        Arrays.stream(classes).forEach(this::putResolver);
    }

    private boolean isNotNamed(Class<?> type) {
        return !Arrays.stream(type.getInterfaces()).map(Class::getSimpleName).filter(i -> i.equals("Named")).collect(Collectors.toSet()).contains("Named");
    }

    @SuppressWarnings("unchecked")
    private void putResolver(Class<? extends Named<? extends Comparable<?>>> type) {
        resolvers.put(type, new DAOEntityTypeResolver(type, manager));
    }

    @SuppressWarnings("unchecked")
    public <T extends Persistable & Named<U>, U extends Comparable<? super U>>
    Collection<T> getAllEntities(Class<T> entityType) {
        DAOTransactional<T, U> resolver = (DAOTransactional<T, U>) resolvers.get(entityType);
        return resolver.getAllItems();
    }

    @SuppressWarnings("unchecked")
    public <T extends Persistable & Named<U>, U extends Comparable<? super U>>
    T getEntity(Class<T> entityType, U name) {
        DAOTransactional<T, U> resolver = (DAOTransactional<T, U>) resolvers.get(entityType);
        return resolver.getItem(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Persistable & Named<U>, U extends Comparable<? super U>>
    Set<T> getEntitiesAsSet(Class<T> type, U... names) {
        Set<T> entities = new HashSet<>();
        for (U n : names) {
            Optional.ofNullable(getEntity(type, n)).ifPresent(entities::add);
        }
        entities.remove(null);
        return entities;
    }

    @SuppressWarnings("unchecked")
    public <T extends Named<U>, U extends Comparable<? super U>>
    List<T> getEntitiesAsList(Class<T> type, U... names) {
        List<T> entities = new ArrayList<>();
        for (U n : names) {
            Optional.ofNullable(getEntity(type, n)).ifPresent(entities::add);
        }
        entities.remove(null);
        return entities;
    }

    public <T extends Named<U>, U extends Comparable<? super U>> void saveEntity(T entity) {
        getResolver(entity).commit(entity);
    }

    public <T extends Named<U>, U extends Comparable<? super U>> void removeEntity(T entity) {
        getResolver(entity).remove(entity.getName());
    }

    @SuppressWarnings("unchecked")
    public <T extends Named<U>, U extends Comparable<? super U>> void removeEntity(Class<T> entityType, U criteria) {
        DAOTransactional<T, U> resolver = (DAOTransactional<T, U>) resolvers.get(entityType);
        resolver.remove(criteria);
    }

    @SuppressWarnings("unchecked")
    private <T extends Named<U>, U extends Comparable<? super U>> DAOTransactional<T, U> getResolver(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        DAOTransactional<T, U> resolver = (DAOTransactional<T, U>) resolvers.get(entity.getClass());
        if (resolver == null) {
            throw new IllegalStateException("no EntityResolver for class " + entity.getClass());
        }
        return resolver;
    }

    public void close() {
        manager.close();
    }
}
