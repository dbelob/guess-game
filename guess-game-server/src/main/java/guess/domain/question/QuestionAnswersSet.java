package guess.domain.question;

import guess.domain.source.LocaleItem;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Question and answers set.
 */
public class QuestionAnswersSet implements Serializable {
    public static final int QUESTION_ANSWERS_LIST_SIZE = 4;

    private final List<LocaleItem> name;
    private final String logoFileName;
    private final List<QuestionAnswers> questionAnswersList;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionAnswersSet that = (QuestionAnswersSet) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(logoFileName, that.logoFileName) &&
                Objects.equals(questionAnswersList, that.questionAnswersList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, logoFileName, questionAnswersList);
    }
}
