package guess.dao;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.question.Question;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;
import guess.domain.source.*;
import guess.util.LocalizationUtils;
import guess.util.QuestionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Question DAO implementation.
 */
@Repository
public class QuestionDaoImpl implements QuestionDao {
    private final EventTypeDao eventTypeDao;
    private final List<QuestionSet> questionSets;
    private final List<Event> events;

    @Autowired
    public QuestionDaoImpl(EventTypeDao eventTypeDao, EventDao eventDao) throws IOException, SpeakerDuplicatedException {
        this.eventTypeDao = eventTypeDao;
        this.questionSets = readQuestionSets();
        this.events = eventDao.getEvents().stream()
                .filter(e -> (e.getStartDate() != null) && (e.getEndDate() != null) && !e.getStartDate().isAfter(e.getEndDate()))
                .sorted(Comparator.comparing(Event::getStartDate))
                .collect(Collectors.toList());
    }

    /**
     * Reads question sets.
     *
     * @return question sets
     */
    public List<QuestionSet> readQuestionSets() {
        // Create question sets
        List<QuestionSet> questionSets = new ArrayList<>();
        for (EventType eventType : eventTypeDao.getEventTypes()) {
            // Fill speaker and talk questions
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();
            List<TalkQuestion> talkQuestions = new ArrayList<>();

            for (Event event : eventType.getEvents()) {
                for (Talk talk : event.getTalks()) {
                    for (Speaker speaker : talk.getSpeakers()) {
                        speakerQuestions.add(new SpeakerQuestion(speaker));
                    }

                    talkQuestions.add(new TalkQuestion(
                            talk.getSpeakers(),
                            talk));
                }
            }

            questionSets.add(new QuestionSet(
                    eventType.getId(),
                    createEventTypeDisplayName(eventType),
                    eventType.getLogoFileName(),
                    QuestionUtils.removeDuplicatesById(speakerQuestions),
                    QuestionUtils.removeDuplicatesById(talkQuestions)));
        }

        return questionSets;
    }

    /**
     * Creates name with prefix.
     *
     * @param eventType event type
     * @return name with prefix
     */
    private static List<LocaleItem> createEventTypeDisplayName(EventType eventType) {
        final String CONFERENCES_EVENT_TYPE_TEXT = "conferencesEventTypeText";
        final String MEETUPS_EVENT_TYPE_TEXT = "meetupsEventTypeText";

        List<LocaleItem> localeItems = new ArrayList<>();
        String resourceKey = (eventType.getConference() != null) ? CONFERENCES_EVENT_TYPE_TEXT : MEETUPS_EVENT_TYPE_TEXT;
        String enText = LocalizationUtils.getString(eventType.getName(), Language.ENGLISH);
        String ruText = LocalizationUtils.getString(eventType.getName(), Language.RUSSIAN);

        if ((enText != null) && !enText.isEmpty()) {
            localeItems.add(new LocaleItem(
                    Language.ENGLISH.getCode(),
                    String.format(LocalizationUtils.getResourceString(resourceKey, Language.ENGLISH), enText)));
        }

        if ((ruText != null) && !ruText.isEmpty()) {
            localeItems.add(new LocaleItem(
                    Language.RUSSIAN.getCode(),
                    String.format(LocalizationUtils.getResourceString(resourceKey, Language.RUSSIAN), ruText)));
        }

        return localeItems;
    }

    @Override
    public List<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    @Override
    public Long getDefaultQuestionSetId(LocalDate date) {
        return events.stream()
                .filter(e -> !date.isAfter(e.getEndDate()))
                .findFirst()
                .map(e -> e.getEventType().getId())
                .orElse(0L);
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
    public List<Question> getQuestionByIds(List<Long> questionSetIds, GuessMode guessMode) throws QuestionSetNotExistsException {
        List<Question> questions;

        if (GuessMode.GUESS_NAME_BY_PHOTO_MODE.equals(guessMode) || GuessMode.GUESS_PHOTO_BY_NAME_MODE.equals(guessMode)) {
            // Guess name by picture or picture by name
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();

            for (Long questionSetId : questionSetIds) {
                speakerQuestions.addAll(getQuestionSetById(questionSetId).getSpeakerQuestions());
            }

            questions = new ArrayList<>(QuestionUtils.removeDuplicatesById(speakerQuestions));
        } else if (GuessMode.GUESS_TALK_BY_SPEAKER_MODE.equals(guessMode) || GuessMode.GUESS_SPEAKER_BY_TALK_MODE.equals(guessMode)) {
            // Guess talk by speaker or speaker by talk
            List<TalkQuestion> talkQuestions = new ArrayList<>();

            for (Long questionSetId : questionSetIds) {
                talkQuestions.addAll(getQuestionSetById(questionSetId).getTalkQuestions());
            }

            questions = new ArrayList<>(QuestionUtils.removeDuplicatesById(talkQuestions));
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess mode: %s", guessMode));
        }

        return questions;
    }
}
