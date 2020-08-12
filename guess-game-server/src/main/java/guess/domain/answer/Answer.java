package guess.domain.answer;

import guess.domain.Identifiable;
import guess.domain.QuestionAnswer;

/**
 * Answer.
 */
public class Answer<T extends Identifiable> extends QuestionAnswer<T> {
    public Answer(T entity) {
        super(entity);
    }
}
