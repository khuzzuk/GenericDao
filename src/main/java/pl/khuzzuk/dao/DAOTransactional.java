package pl.khuzzuk.dao;

import java.util.Collection;

interface DAOTransactional<T extends Named<U>, U extends Comparable<? super U>> {
    Collection<T> getAllItems();

    T getItem(U criteria);

    boolean commit(T toCommit);

    boolean remove(U toRemove);
}
