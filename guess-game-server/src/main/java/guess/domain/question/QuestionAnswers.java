package guess.domain.question;

import java.util.List;

/**
 * Question and answers.
 */
public class QuestionAnswers {
    private final Question question;
    private final List<Question> answers;

    public QuestionAnswers(Question question, List<Question> answers) {
        this.question = question;
        this.answers = answers;
    }

    public Question getQuestion() {
        return question;
    }

    public List<Question> getAnswers() {
        return answers;
    }

}
