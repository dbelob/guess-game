package guess.domain.statistics.olap.measure;

import java.util.HashSet;
import java.util.Set;

/**
 * Measure.
 */
public abstract class Measure<T> {
    private final Class<T> entityClass;
    protected final Set<T> entities = new HashSet<>();

    protected Measure(Class<T> entityClass, Set<Object> entities) {
        this.entityClass = entityClass;

        for (Object entity : entities) {
            this.entities.add(getCheckedEntity(entity));
        }
    }

    private T getCheckedEntity(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Measure entity is null");
        }

        if (!entityClass.isInstance(entity)) {
            throw new IllegalArgumentException(String.format("Invalid measure entity class %s, valid measure entity class is %s",
                    entity.getClass().getSimpleName(), entityClass.getSimpleName()));
        }

        return entityClass.cast(entity);
    }

    public void addEntity(Object entity) {
        this.entities.add(getCheckedEntity(entity));
    }

    public Set<T> getEntities() {
        return entities;
    }

    public abstract long calculateValue();
}
