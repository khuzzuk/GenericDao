package pl.khuzzuk.dao;

public interface Named<T extends Comparable<? super T>> extends Persistable {
    T getName();
    default boolean namedEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Named named = (Named) o;

        return getName() != null ? getName().equals(named.getName()) : named.getName() == null;

    }

    default int namedHashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }
}
