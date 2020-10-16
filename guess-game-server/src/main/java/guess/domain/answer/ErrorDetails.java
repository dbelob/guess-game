package guess.domain.answer;

import guess.domain.question.Question;

import java.util.List;
import java.util.Objects;

/**
 * Error details.
 */
public class ErrorDetails {
    private final Question question;
    private final List<Answer> availableAnswers;
    private final List<Answer> yourAnswers;

    public ErrorDetails(Question question, List<Answer> availableAnswers, List<Answer> yourAnswers) {
        this.question = question;
        this.availableAnswers = availableAnswers;
        this.yourAnswers = yourAnswers;
    }

    public Question getQuestion() {
        return question;
    }

    public List<Answer> getAvailableAnswers() {
        return availableAnswers;
    }

    public List<Answer> getYourAnswers() {
        return yourAnswers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorDetails)) return false;
        ErrorDetails that = (ErrorDetails) o;
        return Objects.equals(question, that.question) &&
                Objects.equals(availableAnswers, that.availableAnswers) &&
                Objects.equals(yourAnswers, that.yourAnswers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, availableAnswers, yourAnswers);
    }

    @Override
    public String toString() {
        return "ErrorDetails{" +
                "question=" + question +
                ", availableAnswers=" + availableAnswers +
                ", yourAnswers=" + yourAnswers +
                '}';
    }
}
