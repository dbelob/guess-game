package guess.domain;

import java.util.List;

/**
 * Question and answers set.
 */
public class QuestionAnswersSet {
    public static final int QUESTION_ANSWERS_LIST_SIZE = 4;

    private String name;
    private String directoryName;
    private String logoFileName;
    private List<QuestionAnswers> questionAnswersList;

    public QuestionAnswersSet(String name, String directoryName, String logoFileName, List<QuestionAnswers> questionAnswersList) {
        this.name = name;
        this.directoryName = directoryName;
        this.logoFileName = logoFileName;
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

    public String getLogoFileName() {
        return logoFileName;
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
