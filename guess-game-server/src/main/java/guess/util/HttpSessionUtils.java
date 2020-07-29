package guess.util;

import guess.domain.Language;
import guess.domain.StartParameters;
import guess.domain.State;
import guess.domain.answer.AnswerSet;
import guess.domain.question.QuestionAnswersSet;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * HttpSession utility methods.
 */
public class HttpSessionUtils {
    private static final String STATE_ATTRIBUTE_NAME = "state";
    private static final String START_PARAMETERS_ATTRIBUTE_NAME = "startParameters";
    private static final String QUESTION_ANSWERS_SET_ATTRIBUTE_NAME = "questionAnswersSet";
    private static final String ANSWER_SETS_ATTRIBUTE_NAME = "answerSets";
    private static final String LANGUAGE_ATTRIBUTE_NAME = "language";

    private HttpSessionUtils() {
    }

    public static State getState(HttpSession httpSession) {
        Object stateObject = httpSession.getAttribute(STATE_ATTRIBUTE_NAME);

        if (stateObject instanceof State) {
            return (State) stateObject;
        } else {
            return State.START_STATE;
        }
    }

    public static void setState(State state, HttpSession httpSession) {
        httpSession.setAttribute(STATE_ATTRIBUTE_NAME, state);
    }

    public static StartParameters getStartParameters(HttpSession httpSession) {
        Object startParametersObject = httpSession.getAttribute(START_PARAMETERS_ATTRIBUTE_NAME);

        if (startParametersObject instanceof StartParameters) {
            return (StartParameters) startParametersObject;
        } else {
            return null;
        }
    }

    public static void setStartParameters(StartParameters startParameters, HttpSession httpSession) {
        httpSession.setAttribute(START_PARAMETERS_ATTRIBUTE_NAME, startParameters);
    }

    public static QuestionAnswersSet getQuestionAnswersSet(HttpSession httpSession) {
        Object questionAnswersSetObject = httpSession.getAttribute(QUESTION_ANSWERS_SET_ATTRIBUTE_NAME);

        if (questionAnswersSetObject instanceof QuestionAnswersSet) {
            return (QuestionAnswersSet) questionAnswersSetObject;
        } else {
            return null;
        }
    }

    public static void setQuestionAnswersSet(QuestionAnswersSet questionAnswersSet, HttpSession httpSession) {
        httpSession.setAttribute(QUESTION_ANSWERS_SET_ATTRIBUTE_NAME, questionAnswersSet);
    }

    @SuppressWarnings("unchecked")
    public static List<AnswerSet> getAnswerSets(HttpSession httpSession) {
        Object answerSetsObject = httpSession.getAttribute(ANSWER_SETS_ATTRIBUTE_NAME);

        if (answerSetsObject instanceof List) {
            return (List<AnswerSet>) answerSetsObject;
        } else {
            List<AnswerSet> answerSets = new ArrayList<>();
            httpSession.setAttribute(ANSWER_SETS_ATTRIBUTE_NAME, answerSets);

            return answerSets;
        }
    }

    public static void clearAnswerSets(HttpSession httpSession) {
        Object answerSetsObject = httpSession.getAttribute(ANSWER_SETS_ATTRIBUTE_NAME);

        if (answerSetsObject instanceof List) {
            ((List) answerSetsObject).clear();
        }
    }

    @SuppressWarnings("unchecked")
    public static void addAnswerSet(AnswerSet answerSet, HttpSession httpSession) {
        Object answerSetsObject = httpSession.getAttribute(ANSWER_SETS_ATTRIBUTE_NAME);

        if (answerSetsObject instanceof List) {
            List<AnswerSet> answerSets = (List<AnswerSet>) answerSetsObject;

            answerSets.add(answerSet);
        } else {
            List<AnswerSet> answerSets = new ArrayList<>();

            answerSets.add(answerSet);
            httpSession.setAttribute(ANSWER_SETS_ATTRIBUTE_NAME, answerSets);
        }
    }

    public static void setLanguage(Language language, HttpSession httpSession) {
        httpSession.setAttribute(LANGUAGE_ATTRIBUTE_NAME, language);
    }

    public static Language getLanguage(HttpSession httpSession) {
        Object languageObject = httpSession.getAttribute(LANGUAGE_ATTRIBUTE_NAME);

        if (languageObject instanceof Language) {
            return (Language) languageObject;
        } else {
            return Language.ENGLISH;
        }
    }
}
