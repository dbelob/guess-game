package guess.domain.question;

import guess.domain.Identifiable;

import java.io.Serializable;

/**
 * Question.
 */
public abstract class Question extends Identifiable implements Serializable {
    public Question(long id) {
        super(id);
    }
}
