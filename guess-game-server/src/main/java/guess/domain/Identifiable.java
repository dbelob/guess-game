package guess.domain;

import java.io.Serializable;

/**
 * Identifiable.
 */
public interface Identifiable extends Serializable {
    long getId();

    void setId(long id);
}
