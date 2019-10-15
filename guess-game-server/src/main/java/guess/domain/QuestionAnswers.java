package guess.domain;

import guess.domain.question.Question;

import java.util.List;

/**
 * Question and answers.
 */
public class QuestionAnswers {
    private Question question;
    private List<Question> answers;

    public QuestionAnswers(Question question, List<Question> answers) {
        this.question = question;
        this.answers = answers;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<Question> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Question> answers) {
        this.answers = answers;
    }
}
