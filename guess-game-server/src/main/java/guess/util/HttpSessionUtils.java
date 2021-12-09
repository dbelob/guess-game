package guess.util;

import guess.domain.GameState;
import guess.domain.Language;
import guess.domain.StartParameters;
import guess.domain.answer.AnswerSet;
import guess.domain.question.QuestionAnswersSet;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * HttpSession utility methods.
 */
public class HttpSessionUtils {
    static final String STATE_ATTRIBUTE_NAME = "state";
    static final String START_PARAMETERS_ATTRIBUTE_NAME = "startParameters";
    static final String QUESTION_ANSWERS_SET_ATTRIBUTE_NAME = "questionAnswersSet";
    static final String ANSWER_SETS_ATTRIBUTE_NAME = "answerSets";
    static final String LANGUAGE_ATTRIBUTE_NAME = "language";

    private HttpSessionUtils() {
    }

    public static GameState getGameState(HttpSession httpSession) {
        var stateObject = httpSession.getAttribute(STATE_ATTRIBUTE_NAME);

        if (stateObject instanceof GameState gameState) {
            return gameState;
        } else {
            return GameState.START_STATE;
        }
    }

    public static void setGameState(GameState state, HttpSession httpSession) {
        httpSession.setAttribute(STATE_ATTRIBUTE_NAME, state);
    }

    public static StartParameters getStartParameters(HttpSession httpSession) {
        var startParametersObject = httpSession.getAttribute(START_PARAMETERS_ATTRIBUTE_NAME);

        if (startParametersObject instanceof StartParameters startParameters) {
            return startParameters;
        } else {
            return null;
        }
    }

    public static void setStartParameters(StartParameters startParameters, HttpSession httpSession) {
        httpSession.setAttribute(START_PARAMETERS_ATTRIBUTE_NAME, startParameters);
    }

    public static void clearStartParameters(HttpSession httpSession) {
        httpSession.removeAttribute(START_PARAMETERS_ATTRIBUTE_NAME);
    }

    public static QuestionAnswersSet getQuestionAnswersSet(HttpSession httpSession) {
        var questionAnswersSetObject = httpSession.getAttribute(QUESTION_ANSWERS_SET_ATTRIBUTE_NAME);

        if (questionAnswersSetObject instanceof QuestionAnswersSet questionAnswersSet) {
            return questionAnswersSet;
        } else {
            return null;
        }
    }

    public static void setQuestionAnswersSet(QuestionAnswersSet questionAnswersSet, HttpSession httpSession) {
        httpSession.setAttribute(QUESTION_ANSWERS_SET_ATTRIBUTE_NAME, questionAnswersSet);
    }

    public static void clearQuestionAnswersSet(HttpSession httpSession) {
        httpSession.removeAttribute(QUESTION_ANSWERS_SET_ATTRIBUTE_NAME);
    }

    @SuppressWarnings("unchecked")
    public static List<AnswerSet> getAnswerSets(HttpSession httpSession) {
        var answerSetsObject = httpSession.getAttribute(ANSWER_SETS_ATTRIBUTE_NAME);

        if (answerSetsObject instanceof List) {
            return (List<AnswerSet>) answerSetsObject;
        } else {
            List<AnswerSet> answerSets = new ArrayList<>();
            httpSession.setAttribute(ANSWER_SETS_ATTRIBUTE_NAME, answerSets);

            return answerSets;
        }
    }

    public static void clearAnswerSets(HttpSession httpSession) {
        httpSession.removeAttribute(ANSWER_SETS_ATTRIBUTE_NAME);
    }

    @SuppressWarnings("unchecked")
    public static void addAnswerSet(AnswerSet answerSet, HttpSession httpSession) {
        var answerSetsObject = httpSession.getAttribute(ANSWER_SETS_ATTRIBUTE_NAME);

        if (answerSetsObject instanceof List) {
            List<AnswerSet> answerSets = (List<AnswerSet>) answerSetsObject;

            answerSets.add(answerSet);
        } else {
            List<AnswerSet> answerSets = new ArrayList<>();

            answerSets.add(answerSet);
            httpSession.setAttribute(ANSWER_SETS_ATTRIBUTE_NAME, answerSets);
        }
    }

    public static Language getLanguage(HttpSession httpSession) {
        var languageObject = httpSession.getAttribute(LANGUAGE_ATTRIBUTE_NAME);

        if (languageObject instanceof Language language) {
            return language;
        } else {
            return Language.ENGLISH;
        }
    }

    public static void setLanguage(Language language, HttpSession httpSession) {
        httpSession.setAttribute(LANGUAGE_ATTRIBUTE_NAME, language);
    }
}
