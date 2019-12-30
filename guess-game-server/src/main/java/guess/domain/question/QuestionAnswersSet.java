package guess.domain.question;

import guess.domain.source.LocaleItem;

import java.util.List;

/**
 * Question and answers set.
 */
public class QuestionAnswersSet {
    public static final int QUESTION_ANSWERS_LIST_SIZE = 4;

    final private List<QuestionAnswers2> questionAnswersList;
    final private List<LocaleItem> name;
    final private String logoFileName;

    public QuestionAnswersSet(List<LocaleItem> name, String logoFileName,
                              List<QuestionAnswers2> questionAnswersList) {
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

    public List<QuestionAnswers2> getQuestionAnswersList() {
        return questionAnswersList;
    }
}
