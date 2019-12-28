package guess.domain.question;

import guess.domain.source.LocaleItem;

import java.util.List;

/**
 * Question and answers set.
 */
public class QuestionAnswersSet {
    public static final int QUESTION_ANSWERS_LIST_SIZE = 4;

    final private List<QuestionAnswers> questionAnswersList;    //TODO: delete
    final private List<QuestionAnswers2> questionAnswersList2;
    final private List<LocaleItem> name;
    final private String logoFileName;

    public QuestionAnswersSet(List<LocaleItem>  name, String logoFileName,
                              List<QuestionAnswers> questionAnswersList,
                              List<QuestionAnswers2> questionAnswersList2) {
        this.name = name;
        this.logoFileName = logoFileName;
        this.questionAnswersList = questionAnswersList;
        this.questionAnswersList2 = questionAnswersList2;
    }

    public List<LocaleItem>  getName() {
        return name;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public List<QuestionAnswers> getQuestionAnswersList() {
        return questionAnswersList;
    }

    public List<QuestionAnswers2> getQuestionAnswersList2() {
        return questionAnswersList2;
    }
}
