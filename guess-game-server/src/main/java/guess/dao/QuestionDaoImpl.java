package guess.dao;

import guess.domain.GuessMode;
import guess.domain.Language;
import guess.domain.question.*;
import guess.domain.source.Company;
import guess.domain.source.Event;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.util.QuestionUtils;
import guess.util.tagcloud.TagCloudUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
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
            // Fill questions
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();
            List<TalkQuestion> talkQuestions = new ArrayList<>();
            List<SpeakerQuestion> accountQuestions = new ArrayList<>();

            Set<Speaker> speakerSet = new HashSet<>();
            Map<Company, Set<Speaker>> companySpeakersMap = new HashMap<>();
            Map<Speaker, Map<Language, StringBuilder>> speakerTalkTextMap = new HashMap<>();

            for (Talk talk : event.getTalks()) {
                for (Speaker speaker : talk.getSpeakers()) {
                    var speakerQuestion = new SpeakerQuestion(speaker);
                    speakerQuestions.add(speakerQuestion);

                    String twitter = speaker.getTwitter();
                    String gitHub = speaker.getGitHub();

                    if (((twitter != null) && !twitter.isEmpty()) ||
                            ((gitHub != null) && !gitHub.isEmpty())) {
                        accountQuestions.add(speakerQuestion);
                    }

                    fillCompanyInformation(speaker, speakerSet, companySpeakersMap);
                }

                talkQuestions.add(new TalkQuestion(
                        talk.getSpeakers(),
                        talk));

                fillSpeakerTalkTextInformation(talk, speakerTalkTextMap);
            }

            List<CompanyBySpeakerQuestion> companyBySpeakerQuestions = speakerSet.stream()
                    .map(s -> new CompanyBySpeakerQuestion(s.getCompanies(), s))
                    .toList();

            List<SpeakerByCompanyQuestion> speakerByCompanyQuestions = companySpeakersMap.keySet().stream()
                    .map(c -> new SpeakerByCompanyQuestion(List.copyOf(companySpeakersMap.get(c)), c))
                    .toList();

            List<TagCloudQuestion> tagCloudQuestions = speakerTalkTextMap.keySet().stream()
                    .map(s -> new TagCloudQuestion(
                            s,
                            speakerTalkTextMap.get(s).entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            e -> TagCloudUtils.getWordFrequenciesByText(
                                                    e.getValue().toString(),
                                                    TagCloudUtils.getSpeakerStopWords(s))
                                    ))
                    ))
                    .toList();

            localQuestionSets.add(new QuestionSet(
                    event,
                    QuestionUtils.removeDuplicatesById(speakerQuestions),
                    QuestionUtils.removeDuplicatesById(talkQuestions),
                    companyBySpeakerQuestions,
                    speakerByCompanyQuestions,
                    QuestionUtils.removeDuplicatesById(accountQuestions),
                    tagCloudQuestions));
        }

        return localQuestionSets;
    }

    /**
     * Fills company information from speaker.
     *
     * @param speaker            speaker
     * @param speakerSet         speaker set
     * @param companySpeakersMap company speakers map
     */
    static void fillCompanyInformation(Speaker speaker, Set<Speaker> speakerSet, Map<Company, Set<Speaker>> companySpeakersMap) {
        if (!speaker.getCompanies().isEmpty()) {
            // Fill speakers with companies set
            speakerSet.add(speaker);

            // Fill company to speakers map
            for (Company company : speaker.getCompanies()) {
                companySpeakersMap.computeIfAbsent(company, k -> new HashSet<>());
                companySpeakersMap.get(company).add(speaker);
            }
        }
    }

    /**
     * Fills speaker text information from talk.
     *
     * @param talk               talk
     * @param speakerTalkTextMap speaker, talk text map
     */
    static void fillSpeakerTalkTextInformation(Talk talk, Map<Speaker, Map<Language, StringBuilder>> speakerTalkTextMap) {
        if (talk.getSpeakers().size() == 1) {
            var speaker = talk.getSpeakers().get(0);

            speakerTalkTextMap.computeIfAbsent(speaker, k -> new EnumMap<>(Language.class));
            speakerTalkTextMap.get(speaker).computeIfAbsent(Language.ENGLISH, k -> new StringBuilder());
            speakerTalkTextMap.get(speaker).get(Language.ENGLISH).append(TagCloudUtils.getTalkText(talk, Language.ENGLISH));

            var talkLanguage = Language.getLanguageByCode(talk.getLanguage());

            if ((talkLanguage != null) && !Language.ENGLISH.equals(talkLanguage) && TagCloudUtils.isTalkTextExists(talk, talkLanguage)) {
                speakerTalkTextMap.get(speaker).computeIfAbsent(talkLanguage, k -> new StringBuilder());
                speakerTalkTextMap.get(speaker).get(talkLanguage).append(TagCloudUtils.getTalkText(talk, talkLanguage));
            }
        }
    }

    @Override
    public List<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    @Override
    public List<QuestionSet> getSubQuestionSets(List<Long> eventTypeIds, List<Long> eventIds) {
        return EventTypeDao.getItemsByEventTypeIds(eventTypeIds,
                eventTypeId -> questionSets.stream()
                        .filter(s -> ((s.getEvent().getEventType().getId() == eventTypeId) && eventIds.contains(s.getEvent().getId())))
                        .toList(),
                v -> questionSets.stream()
                        .filter(s -> eventTypeIds.contains(s.getEvent().getEventType().getId()))
                        .toList(),
                eventTypeDao);
    }

    @Override
    public List<Question> getQuestionByIds(List<Long> eventTypeIds, List<Long> eventIds, GuessMode guessMode) {
        // Find sub question sets
        List<QuestionSet> subQuestionSets = getSubQuestionSets(eventTypeIds, eventIds);

        if (GuessMode.GUESS_NAME_BY_PHOTO_MODE.equals(guessMode) || GuessMode.GUESS_PHOTO_BY_NAME_MODE.equals(guessMode)) {
            // Guess name by photo or photo by name
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();

            subQuestionSets.stream()
                    .map(QuestionSet::getSpeakerQuestions)
                    .forEach(speakerQuestions::addAll);

            return new ArrayList<>(QuestionUtils.removeDuplicatesById(speakerQuestions));
        } else if (GuessMode.GUESS_TALK_BY_SPEAKER_MODE.equals(guessMode) || GuessMode.GUESS_SPEAKER_BY_TALK_MODE.equals(guessMode)) {
            // Guess talk by speaker or speaker by talk
            List<TalkQuestion> talkQuestions = new ArrayList<>();

            subQuestionSets.stream()
                    .map(QuestionSet::getTalkQuestions)
                    .forEach(talkQuestions::addAll);

            return new ArrayList<>(QuestionUtils.removeDuplicatesById(talkQuestions));
        } else if (GuessMode.GUESS_COMPANY_BY_SPEAKER_MODE.equals(guessMode)) {
            // Guess company by speaker
            List<CompanyBySpeakerQuestion> companyBySpeakerQuestions = new ArrayList<>();

            subQuestionSets.stream()
                    .map(QuestionSet::getCompanyBySpeakerQuestions)
                    .forEach(companyBySpeakerQuestions::addAll);

            return new ArrayList<>(QuestionUtils.removeDuplicatesById(companyBySpeakerQuestions));
        } else if (GuessMode.GUESS_SPEAKER_BY_COMPANY_MODE.equals(guessMode)) {
            return getSpeakerByCompanyQuestions(subQuestionSets);
        } else if (GuessMode.GUESS_ACCOUNT_BY_SPEAKER_MODE.equals(guessMode) || GuessMode.GUESS_SPEAKER_BY_ACCOUNT_MODE.equals(guessMode)) {
            // Guess accounts by speaker or speaker by accounts
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();

            subQuestionSets.stream()
                    .map(QuestionSet::getAccountQuestions)
                    .forEach(speakerQuestions::addAll);

            return new ArrayList<>(QuestionUtils.removeDuplicatesById(speakerQuestions));
        } else if (GuessMode.GUESS_TAG_CLOUD_BY_SPEAKER_MODE.equals(guessMode) || GuessMode.GUESS_SPEAKER_BY_TAG_CLOUD_MODE.equals(guessMode)) {
            return getTagCloudBySpeakerQuestions(subQuestionSets);
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess mode: %s", guessMode));
        }
    }

    /**
     * Gets speaker by company questions.
     *
     * @param questionSets question sets
     * @return speaker by company questions
     */
    static List<Question> getSpeakerByCompanyQuestions(List<QuestionSet> questionSets) {
        // Guess speaker by company
        Map<Company, Set<Speaker>> companySpeakersMap = new HashMap<>();

        for (QuestionSet questionSet : questionSets) {
            for (SpeakerByCompanyQuestion question : questionSet.getSpeakerByCompanyQuestions()) {
                if (!companySpeakersMap.containsKey(question.getCompany())) {
                    companySpeakersMap.put(question.getCompany(), new HashSet<>());
                }

                companySpeakersMap.get(question.getCompany()).addAll(question.getSpeakers());
            }
        }

        return companySpeakersMap.keySet().stream()
                .map(c -> (Question) new SpeakerByCompanyQuestion(List.copyOf(companySpeakersMap.get(c)), c))
                .toList();
    }

    /**
     * Gets tag cloud by speakers questions.
     *
     * @param questionSets question sets
     * @return tag cloud by speakers questions
     */
    static List<Question> getTagCloudBySpeakerQuestions(List<QuestionSet> questionSets) {
        Map<Speaker, List<TagCloudQuestion>> speakerQuestionsMap = new HashMap<>();

        for (QuestionSet questionSet : questionSets) {
            for (TagCloudQuestion question : questionSet.getTagCloudBySpeakerQuestions()) {
                var speaker = question.getSpeaker();

                speakerQuestionsMap.computeIfAbsent(speaker, k -> new ArrayList<>());
                speakerQuestionsMap.get(speaker).add(question);
            }
        }

        return speakerQuestionsMap.keySet().stream()
                .map(s -> (Question) new TagCloudQuestion(
                        s,
                        TagCloudUtils.mergeWordFrequenciesMaps(
                                speakerQuestionsMap.get(s).stream()
                                        .map(TagCloudQuestion::getLanguageWordFrequenciesMap)
                                        .toList()
                        )
                ))
                .toList();
    }
}
