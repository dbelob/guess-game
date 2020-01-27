package guess.util;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Conference data loader.
 */
public class ConferenceDataLoader {
    private static final Logger log = LoggerFactory.getLogger(ConferenceDataLoader.class);

    /**
     * Loads all conference event types.
     */
    private static void loadEventTypes() {
        List<EventType> eventTypes = ContentfulUtils.getEventTypes();

        log.info("Event types: {}", eventTypes.size());
        eventTypes.forEach(
                et -> log.info("Event type: conference: {}, nameEn: {}, nameRu: {}, siteLinkEn: {}, siteLinkRu: {}",
                        et.getConference(),
                        LocalizationUtils.getString(et.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(et.getName(), Language.RUSSIAN),
                        LocalizationUtils.getString(et.getSiteLink(), Language.ENGLISH),
                        LocalizationUtils.getString(et.getSiteLink(), Language.RUSSIAN))
        );

        //TODO: implement comparing and YAML file saving
    }

    /**
     * Loads talks, speakers, event information.
     *
     * @param conference     conference
     * @param startDate      start date
     * @param conferenceCode conference code
     * @throws IOException                if resource files could not be opened
     * @throws SpeakerDuplicatedException if speakers duplicated
     */
    private static void loadTalksSpeakersEvent(Conference conference, LocalDate startDate, String conferenceCode) throws IOException, SpeakerDuplicatedException {
        log.info("{} {} {}", conference, startDate, conferenceCode);

        // Read event types, events, speakers, talks from resource files
        SourceInformation resourceSourceInformation = YamlUtils.readSourceInformation();

        Optional<EventType> resourceOptionalEventType = resourceSourceInformation.getEventTypes().stream()
                .filter(et -> et.getConference().equals(conference))
                .findFirst();
        EventType resourceEventType = resourceOptionalEventType
                .orElse(null);

        Event resourceEvent = resourceOptionalEventType
                .flatMap(et -> et.getEvents().stream()
                        .filter(e -> e.getStartDate().equals(startDate))
                        .findFirst())
                .orElse(null);

        // Read event from Contentful
        Event contentfulEvent = ContentfulUtils.getEvent(conference, startDate);
        log.info("Event: nameEn: {}, nameRu: {}, startDate: {}, endDate: {}",
                LocalizationUtils.getString(contentfulEvent.getName(), Language.ENGLISH),
                LocalizationUtils.getString(contentfulEvent.getName(), Language.RUSSIAN),
                contentfulEvent.getStartDate(), contentfulEvent.getEndDate());

        // Read talks from Contentful
        List<Talk> contentfulTalks = ContentfulUtils.getTalks(conference, conferenceCode);
        log.info("Talks: {}", contentfulTalks.size());
        contentfulTalks.forEach(
                t -> log.info("Talk: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(t.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(t.getName(), Language.RUSSIAN))
        );

        // Order speakers with talk order
        List<Speaker> contentfulSpeakers = contentfulTalks.stream()
                .flatMap(t -> t.getSpeakers().stream())
                .distinct()
                .collect(Collectors.toList());
        log.info("Speakers: {}", contentfulSpeakers.size());
        contentfulSpeakers.forEach(
                s -> log.info("Speaker: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(s.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(s.getName(), Language.RUSSIAN))
        );

        //TODO: implement comparing and YAML file saving
    }

    public static void main(String[] args) throws IOException, SpeakerDuplicatedException {
//        loadEventTypes();

        // 2016
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2016, 10, 14), "2016Joker");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2016, 12, 7), "2016hel");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2016, 12, 9), "2016msk");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2016, 12, 10), "2016msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2016, 12, 11), "2016msk");

        // 2017
//        loadTalksSpeakersEvent(Conference.JBREAK, LocalDate.of(2017, 4, 4), "2017JBreak");
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2017, 4, 7), "2017JPoint");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2017, 4, 21), "2017spb");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2017, 5, 19), "2017spb");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2017, 6, 2), "2017spb");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2017, 6, 4), "2017spb");
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2017, 10, 20), "2017DevOops");
//        loadTalksSpeakersEvent(Conference.SMART_DATA, LocalDate.of(2017, 10, 20), "2017smartdata");
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2017, 11, 3), "2017Joker");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2017, 11, 11), "2017msk");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2017, 11, 12), "2017msk");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2017, 12, 8), "2017msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2017, 12, 10), "2017msk");

        // 2018
//        loadTalksSpeakersEvent(Conference.JBREAK, LocalDate.of(2018, 3, 4), "2018JBreak");
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2018, 4, 6), "2018JPoint");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2018, 4, 20), "2018spb");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2018, 4, 22), "2018spb");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2018, 5, 17), "2018spb");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2018, 5, 19), "2018spb");
//        loadTalksSpeakersEvent(Conference.TECH_TRAIN, LocalDate.of(2018, 9, 1), "2018tt");
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2018, 10, 14), "2018DevOops");
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2018, 10, 19), "2018Joker");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2018, 11, 22), "2018msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2018, 11, 24), "2018msk");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2018, 12, 6), "2018msk");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2018, 12, 8), "2018msk");

        // 2019
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2019, 4, 5), "2019jpoint");
//        loadTalksSpeakersEvent(Conference.CPP_RUSSIA, LocalDate.of(2019, 4, 19), "2019cpp");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2019, 5, 15), "2019spb");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2019, 5, 17), "2019spb");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2019, 5, 22), "2019spb");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2019, 5, 24), "2019spb");
//        loadTalksSpeakersEvent(Conference.SPTDC, LocalDate.of(2019, 7, 8), "2019sptdc");
//        loadTalksSpeakersEvent(Conference.HYDRA, LocalDate.of(2019, 7, 11), "2019hydra");
//        loadTalksSpeakersEvent(Conference.TECH_TRAIN, LocalDate.of(2019, 8, 24), "2019tt");
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2019, 10, 25), "2019joker");
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2019, 10, 29), "2019devoops");
//        loadTalksSpeakersEvent(Conference.CPP_RUSSIA, LocalDate.of(2019, 10, 31), "2019-spb-cpp");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2019, 11, 6), "2019msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2019, 11, 8), "2019msk");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2019, 12, 5), "2019msk");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2019, 12, 7), "2019msk");

        // 2020
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2020, 4, 6), "2020-spb");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2020, 4, 8), "2020-spb");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2020, 4, 10), "2020-spb");
//        loadTalksSpeakersEvent(Conference.CPP_RUSSIA, LocalDate.of(2020, 4, 27), "2020-msk-cpp");
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2020, 4, 29), "2020-msk-devoops");
//        loadTalksSpeakersEvent(Conference.JPOINT, LocalDate.of(2020, 5, 15), "2020-jpoint");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2020, 6, 23), "2020-spb");
//        loadTalksSpeakersEvent(Conference.SPTDC, LocalDate.of(2020, 7, 8), "2020-spb-sptdc");         // valid date?
//        loadTalksSpeakersEvent(Conference.HYDRA, LocalDate.of(2020, 7, 11), "2020-spb-hydra");        // valid date?
//        loadTalksSpeakersEvent(Conference.TECH_TRAIN, LocalDate.of(2020, 8, 24), "2020-techtrain");    // valid date?
//        loadTalksSpeakersEvent(Conference.DEV_OOPS, LocalDate.of(2020, 10, 7), "2020-spb-devoops");
//        loadTalksSpeakersEvent(Conference.CPP_RUSSIA, LocalDate.of(2020, 10, 9), "2020-spb-cpp");
//        loadTalksSpeakersEvent(Conference.JOKER, LocalDate.of(2020, 10, 23), "2020-joker");
//        loadTalksSpeakersEvent(Conference.HEISENBUG, LocalDate.of(2020, 11, 3), "2020-msk");
//        loadTalksSpeakersEvent(Conference.DOT_NEXT, LocalDate.of(2020, 11, 5), "2020-msk");
//        loadTalksSpeakersEvent(Conference.HOLY_JS, LocalDate.of(2020, 11, 8), "2020-msk");
//        loadTalksSpeakersEvent(Conference.MOBIUS, LocalDate.of(2020, 12, 7), "2020-msk");             // valid date?
    }
}
