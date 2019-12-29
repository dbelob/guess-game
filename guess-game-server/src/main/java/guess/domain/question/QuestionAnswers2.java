package guess.domain.question;

import guess.domain.Identifiable;

import java.util.List;

/**
 * Question and answers.
 */
public class QuestionAnswers2<T extends Identifiable, S extends Identifiable> {
    private final T question;
    private final List<S> correctAnswers;
    private final List<S> availableAnswers;

    public QuestionAnswers2(T question, List<S> correctAnswers, List<S> availableAnswers) {
        this.question = question;
        this.correctAnswers = correctAnswers;
        this.availableAnswers = availableAnswers;
    }

    public T getQuestion() {
        return question;
    }

    public List<S> getCorrectAnswers() {
        return correctAnswers;
    }

    public List<S> getAvailableAnswers() {
        return availableAnswers;
    }
}
