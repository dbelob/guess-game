package guess.domain.question;

import guess.domain.answer.Answer;

import java.util.List;

/**
 * Question and answers.
 */
public class QuestionAnswers {
    private final Question question;
    private final List<Answer> correctAnswers;
    private final List<Answer> availableAnswers;

    public QuestionAnswers(Question question, List<Answer> correctAnswers, List<Answer> availableAnswers) {
        this.question = question;
        this.correctAnswers = correctAnswers;
        this.availableAnswers = availableAnswers;
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
}
