package guess.domain.question;

import java.util.List;

/**
 * Question and answers.
 */
public class QuestionAnswers2<T, S> {
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
