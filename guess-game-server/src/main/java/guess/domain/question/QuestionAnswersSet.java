package guess.domain.question;

import java.util.List;

/**
 * Question and answers set.
 */
public class QuestionAnswersSet {
    public static final int QUESTION_ANSWERS_LIST_SIZE = 4;

    final private List<QuestionAnswers> questionAnswersList;
    final private String name;
    final private String logoFileName;

    public QuestionAnswersSet(String name, String logoFileName, List<QuestionAnswers> questionAnswersList) {
        this.name = name;
        this.logoFileName = logoFileName;
        this.questionAnswersList = questionAnswersList;
    }

    public String getName() {
        return name;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public List<QuestionAnswers> getQuestionAnswersList() {
        return questionAnswersList;
    }

}
