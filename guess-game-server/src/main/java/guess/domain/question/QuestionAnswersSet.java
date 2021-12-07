package guess.domain.question;

import guess.domain.source.LocaleItem;

import java.io.Serializable;
import java.util.List;

/**
 * Question and answers set.
 */
public record QuestionAnswersSet(List<LocaleItem> name, String logoFileName,
                                 List<QuestionAnswers> questionAnswersList) implements Serializable {
    public static final int QUESTION_ANSWERS_LIST_SIZE = 4;
}
