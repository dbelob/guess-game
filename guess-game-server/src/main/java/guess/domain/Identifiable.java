package guess.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Identifiable.
 */
public abstract class Identifiable implements Serializable {
    private final long id;

    public Identifiable(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifiable that = (Identifiable) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
