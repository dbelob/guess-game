package guess.domain.question;

import guess.domain.source.LocaleItem;

import java.io.Serializable;
import java.util.List;

/**
 * Question and answers set.
 */
public class QuestionAnswersSet implements Serializable {
    public static final int QUESTION_ANSWERS_LIST_SIZE = 4;

    private final List<QuestionAnswers> questionAnswersList;
    private final List<LocaleItem> name;
    private final String logoFileName;

    public QuestionAnswersSet(List<LocaleItem> name, String logoFileName,
                              List<QuestionAnswers> questionAnswersList) {
        this.name = name;
        this.logoFileName = logoFileName;
        this.questionAnswersList = questionAnswersList;
    }

    public List<LocaleItem> getName() {
        return name;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public List<QuestionAnswers> getQuestionAnswersList() {
        return questionAnswersList;
    }
}
