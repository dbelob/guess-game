package acme.guess.domain;

import java.util.List;

/**
 * Question and answers set.
 */
public class QuestionAnswersSet {
    public static final int QUESTION_ANSWERS_LIST_SIZE = 4;

    private String name;
    private String directoryName;
    private List<QuestionAnswers> questionAnswersList;

    public QuestionAnswersSet(String name, String directoryName, List<QuestionAnswers> questionAnswersList) {
        this.name = name;
        this.directoryName = directoryName;
        this.questionAnswersList = questionAnswersList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public List<QuestionAnswers> getQuestionAnswersList() {
        return questionAnswersList;
    }

    public void setQuestionAnswersList(List<QuestionAnswers> questionAnswersList) {
        this.questionAnswersList = questionAnswersList;
    }
}
