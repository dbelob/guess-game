package guess.domain.question;

import guess.domain.Identifiable;
import guess.domain.QuestionAnswer;

/**
 * Question.
 */
public abstract class Question<T extends Identifiable> extends QuestionAnswer<T> {
    public Question(T entity) {
        super(entity);
    }
}
