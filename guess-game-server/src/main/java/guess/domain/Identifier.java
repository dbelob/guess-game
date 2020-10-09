package guess.domain;

import java.util.Objects;

/**
 * Identifier.
 */
public abstract class Identifier implements Identifiable {
    private long id;

    protected Identifier() {
    }

    protected Identifier(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
