package guess.dao;

import guess.domain.GuessMode;
import guess.domain.question.Question;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;
import guess.domain.source.Event;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.util.QuestionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Question DAO implementation.
 */
@Repository
public class QuestionDaoImpl implements QuestionDao {
    private final EventTypeDao eventTypeDao;
    private final EventDao eventDao;
    private final List<QuestionSet> questionSets;

    @Autowired
    public QuestionDaoImpl(EventTypeDao eventTypeDao, EventDao eventDao) {
        this.eventTypeDao = eventTypeDao;
        this.eventDao = eventDao;
        this.questionSets = readQuestionSets();
    }

    /**
     * Reads question sets.
     *
     * @return question sets
     */
    @Override
    public List<QuestionSet> readQuestionSets() {
        // Create question sets
        List<QuestionSet> localQuestionSets = new ArrayList<>();

        for (Event event : eventDao.getEvents()) {
            // Fill speaker and talk questions
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();
            List<TalkQuestion> talkQuestions = new ArrayList<>();
            List<SpeakerQuestion> accountQuestions = new ArrayList<>();

            for (Talk talk : event.getTalks()) {
                for (Speaker speaker : talk.getSpeakers()) {
                    SpeakerQuestion speakerQuestion = new SpeakerQuestion(speaker);
                    speakerQuestions.add(speakerQuestion);

                    String twitter = speaker.getTwitter();
                    String gitHub = speaker.getGitHub();

                    if (((twitter != null) && !twitter.isEmpty()) ||
                            ((gitHub != null) && !gitHub.isEmpty())) {
                        accountQuestions.add(speakerQuestion);
                    }
                }

                talkQuestions.add(new TalkQuestion(
                        talk.getSpeakers(),
                        talk));
            }

            localQuestionSets.add(new QuestionSet(
                    event,
                    QuestionUtils.removeDuplicatesById(speakerQuestions),
                    QuestionUtils.removeDuplicatesById(talkQuestions),
                    QuestionUtils.removeDuplicatesById(accountQuestions)));
        }

        return localQuestionSets;
    }

    @Override
    public List<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    @Override
    public List<Question> getQuestionByIds(List<Long> eventTypeIds, List<Long> eventIds, GuessMode guessMode) {
        List<Question> questions;

        // Find sub question sets
        List<QuestionSet> subQuestionSets = getSubQuestionSets(eventTypeIds, eventIds);

        if (GuessMode.GUESS_NAME_BY_PHOTO_MODE.equals(guessMode) || GuessMode.GUESS_PHOTO_BY_NAME_MODE.equals(guessMode)) {
            // Guess name by photo or photo by name
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();

            subQuestionSets.stream()
                    .map(QuestionSet::getSpeakerQuestions)
                    .forEach(speakerQuestions::addAll);

            questions = new ArrayList<>(QuestionUtils.removeDuplicatesById(speakerQuestions));
        } else if (GuessMode.GUESS_TALK_BY_SPEAKER_MODE.equals(guessMode) || GuessMode.GUESS_SPEAKER_BY_TALK_MODE.equals(guessMode)) {
            // Guess talk by speaker or speaker by talk
            List<TalkQuestion> talkQuestions = new ArrayList<>();

            subQuestionSets.stream()
                    .map(QuestionSet::getTalkQuestions)
                    .forEach(talkQuestions::addAll);

            questions = new ArrayList<>(QuestionUtils.removeDuplicatesById(talkQuestions));
        } else if (GuessMode.GUESS_ACCOUNT_BY_SPEAKER_MODE.equals(guessMode) || GuessMode.GUESS_SPEAKER_BY_ACCOUNT_MODE.equals(guessMode)) {
            // Guess accounts by speaker or speaker by accounts
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();

            subQuestionSets.stream()
                    .map(QuestionSet::getAccountQuestions)
                    .forEach(speakerQuestions::addAll);

            questions = new ArrayList<>(QuestionUtils.removeDuplicatesById(speakerQuestions));
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess mode: %s", guessMode));
        }

        return questions;
    }

    @Override
    public List<QuestionSet> getSubQuestionSets(List<Long> eventTypeIds, List<Long> eventIds) {
        return eventTypeDao.getItemsByEventTypeIds(eventTypeIds,
                eventTypeId -> questionSets.stream()
                        .filter(s -> ((s.getEvent().getEventType().getId() == eventTypeId) && eventIds.contains(s.getEvent().getId())))
                        .collect(Collectors.toList()),
                v -> questionSets.stream()
                        .filter(s -> eventTypeIds.contains(s.getEvent().getEventType().getId()))
                        .collect(Collectors.toList()));
    }
}
