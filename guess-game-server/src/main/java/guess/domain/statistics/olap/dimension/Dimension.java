package guess.domain.statistics.olap.dimension;

import java.util.Objects;

/**
 * Dimension.
 */
public abstract class Dimension<T> {
    private final T value;

    protected Dimension(Class<T> valueClass, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Dimension value is null");
        }

        if (!valueClass.isInstance(value)) {
            throw new IllegalArgumentException(String.format("Invalid dimension value class %s, valid dimension value class is %s",
                    value.getClass().getSimpleName(), valueClass.getSimpleName()));
        }

        this.value = valueClass.cast(value);
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dimension)) return false;
        Dimension<?> dimension = (Dimension<?>) o;
        return Objects.equals(getValue(), dimension.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
