package guess.dao;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessType;
import guess.domain.QuestionSet;
import guess.domain.question.Question;
import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;
import guess.util.QuestionUtils;
import guess.util.YamlUtils;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Question DAO implementation.
 */
@Repository
public class QuestionDaoImpl implements QuestionDao {
    private static String QUESTIONS_DIRECTORY_NAME = "questions";
    private static String DESCRIPTIONS_DIRECTORY_NAME = "descriptions";

    private final List<QuestionSet> questionSets;

    public QuestionDaoImpl() throws IOException {
        this.questionSets = YamlUtils.readQuestionSets(QUESTIONS_DIRECTORY_NAME, DESCRIPTIONS_DIRECTORY_NAME);
    }

    @Override
    public List<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    @Override
    public QuestionSet getQuestionSetById(long id) throws QuestionSetNotExistsException {
        Optional<QuestionSet> optional = questionSets.stream()
                .filter(q -> q.getId() == id)
                .findFirst();

        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new QuestionSetNotExistsException();
        }
    }

    @Override
    public List<Question> getQuestionByIds(List<Long> questionSetIds, GuessType guessType) throws QuestionSetNotExistsException {
        List<Question> questions;

        if (GuessType.GUESS_NAME_TYPE.equals(guessType) || GuessType.GUESS_PICTURE_TYPE.equals(guessType)) {
            // Guess name by picture or picture by name
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();

            for (Long questionSetId : questionSetIds) {
                QuestionSet questionSet = getQuestionSetById(questionSetId);
                List<SpeakerQuestion> questionsWithFullFileName = questionSet.getSpeakerQuestions().stream()
                        .map(q -> new SpeakerQuestion(
                                q.getId(),
                                q.getFileName(),
                                q.getName()))
                        .collect(Collectors.toList());
                speakerQuestions.addAll(questionsWithFullFileName);
            }

            questions = new ArrayList<>(QuestionUtils.removeDuplicatesByFileName(speakerQuestions));
        } else if (GuessType.GUESS_TALK_TYPE.equals(guessType) || GuessType.GUESS_SPEAKER_TYPE.equals(guessType)) {
            // Guess talk by speaker or speaker by talk
            List<TalkQuestion> talkQuestions = new ArrayList<>();

            for (Long questionSetId : questionSetIds) {
                talkQuestions.addAll(getQuestionSetById(questionSetId).getTalkQuestions());
            }

            questions = new ArrayList<>(QuestionUtils.removeDuplicatesById(talkQuestions));
        } else {
            questions = new ArrayList<>();
        }

        return questions;
    }
}
