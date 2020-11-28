package guess.domain.answer;

import guess.domain.question.Question;

import java.util.List;
import java.util.Objects;

/**
 * Error details.
 */
public class ErrorDetails {
    private final Question question;
    private final List<Answer> correctAnswers;
    private final List<Answer> availableAnswers;
    private final List<Answer> yourAnswers;

    public ErrorDetails(Question question, List<Answer> correctAnswers, List<Answer> availableAnswers, List<Answer> yourAnswers) {
        this.question = question;
        this.correctAnswers = correctAnswers;
        this.availableAnswers = availableAnswers;
        this.yourAnswers = yourAnswers;
    }

    public Question getQuestion() {
        return question;
    }

    public List<Answer> getCorrectAnswers() {
        return correctAnswers;
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
                Objects.equals(correctAnswers, that.correctAnswers) &&
                Objects.equals(availableAnswers, that.availableAnswers) &&
                Objects.equals(yourAnswers, that.yourAnswers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, correctAnswers, availableAnswers, yourAnswers);
    }

    @Override
    public String toString() {
        return "ErrorDetails{" +
                "question=" + question +
                ", correctAnswers=" + correctAnswers +
                ", availableAnswers=" + availableAnswers +
                ", yourAnswers=" + yourAnswers +
                '}';
    }
}
