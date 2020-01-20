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

    private static void load(Conference conference, LocalDate startDate, String conferenceCode) throws IOException, SpeakerDuplicatedException {
        // Read event types, events, speakers, talks from resource files
        SourceInformation sourceInformation = YamlUtils.readSourceInformation();
        Optional<EventType> eventTypeOptional = sourceInformation.getEventTypes().stream()
                .filter(et -> et.getConference().equals(conference))
                .findFirst();
        Optional<Event> eventOptional = eventTypeOptional.flatMap(
                et -> et.getEvents().stream()
                        .filter(e -> e.getStartDate().equals(startDate))
                        .findFirst());

        // Read speakers from Contentful
        List<Speaker> speakers = ContentfulUtils.getSpeakers(conference, conferenceCode);
        log.info("Speakers: {}", speakers.size());

        // Read talks from Contentful
        List<Talk> talks = ContentfulUtils.getTalks(conference, conferenceCode);
        log.info("Talks: {}", talks.size());
        talks.forEach(
                t -> log.info("Talk: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(t.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(t.getName(), Language.RUSSIAN))
        );

        // Order speakers with talk order
        List<Speaker> speakersWithTalkOrder = talks.stream()
                .flatMap(t -> t.getSpeakers().stream())
                .distinct()
                .collect(Collectors.toList());
        log.info("Speakers with talk order: {}", speakersWithTalkOrder.size());
        speakersWithTalkOrder.forEach(
                s -> log.info("Speaker with talk order: nameEn: '{}', name: '{}'",
                        LocalizationUtils.getString(s.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(s.getName(), Language.RUSSIAN))
        );
    }

    public static void main(String[] args) throws IOException, SpeakerDuplicatedException {
        // 2016
        load(Conference.JOKER, LocalDate.of(2016, 10, 14), "2016Joker");
//        load(Conference.DOT_NEXT, LocalDate.of(2016, 12, 7), "2016hel");
//        load(Conference.DOT_NEXT, LocalDate.of(2016, 12, 9), "2016msk");
//        load(Conference.HEISENBUG, LocalDate.of(2016, 12, 10), "2016msk");
//        load(Conference.HOLY_JS, LocalDate.of(2016, 12, 11), "2016msk");

        // 2017
//        load(Conference.JBREAK, LocalDate.of(2017, 4, 4), "2017JBreak");
//        load(Conference.JPOINT, LocalDate.of(2017, 4, 7), "2017JPoint");
//        load(Conference.MOBIUS, LocalDate.of(2017, 4, 21), "2017spb");
//        load(Conference.DOT_NEXT, LocalDate.of(2017, 5, 19), "2017spb");
//        load(Conference.HOLY_JS, LocalDate.of(2017, 6, 2), "2017spb");
//        load(Conference.HEISENBUG, LocalDate.of(2017, 6, 4), "2017spb");
//        load(Conference.DEV_OOPS, LocalDate.of(2017, 10, 20), "2017DevOops");
//        load(Conference.SMART_DATA, LocalDate.of(2017, 10, 20), "2017smartdata");
//        load(Conference.JOKER, LocalDate.of(2017, 11, 3), "2017Joker");
//        load(Conference.MOBIUS, LocalDate.of(2017, 11, 11), "2017msk");
//        load(Conference.DOT_NEXT, LocalDate.of(2017, 11, 12), "2017msk");
//        load(Conference.HEISENBUG, LocalDate.of(2017, 12, 8), "2017msk");
//        load(Conference.HOLY_JS, LocalDate.of(2017, 12, 10), "2017msk");

        // 2018
//        load(Conference.JBREAK, LocalDate.of(2018, 3, 4), "2018JBreak");
//        load(Conference.JPOINT, LocalDate.of(2018, 4, 6), "2018JPoint");
//        load(Conference.MOBIUS, LocalDate.of(2018, 4, 20), "2018spb");
//        load(Conference.DOT_NEXT, LocalDate.of(2018, 4, 22), "2018spb");
//        load(Conference.HEISENBUG, LocalDate.of(2018, 5, 17), "2018spb");
//        load(Conference.HOLY_JS, LocalDate.of(2018, 5, 19), "2018spb");
//        load(Conference.TECH_TRAIN,LocalDate.of(2018, 9, 1), "2018tt");
//        load(Conference.DEV_OOPS, LocalDate.of(2018, 10, 14), "2018DevOops");
//        load(Conference.JOKER, LocalDate.of(2018, 10, 19), "2018Joker");
//        load(Conference.DOT_NEXT, LocalDate.of(2018, 11, 22), "2018msk");
//        load(Conference.HOLY_JS, LocalDate.of(2018, 11, 24), "2018msk");
//        load(Conference.HEISENBUG, LocalDate.of(2018, 12, 6), "2018msk");
//        load(Conference.MOBIUS, LocalDate.of(2018, 12, 8), "2018msk");

        // 2019
//        load(Conference.JPOINT, LocalDate.of(2019, 4, 5), "2019jpoint");
//        load(Conference.CPP_RUSSIA, LocalDate.of(2019, 4, 19), "2019cpp");
//        load(Conference.DOT_NEXT, LocalDate.of(2019, 5, 15), "2019spb");
//        load(Conference.HEISENBUG, LocalDate.of(2019, 5, 17), "2019spb");
//        load(Conference.MOBIUS, LocalDate.of(2019, 5, 22), "2019spb");
//        load(Conference.HOLY_JS, LocalDate.of(2019, 5, 24), "2019spb");
//        load(Conference.SPTDC, LocalDate.of(2019, 7, 8), "2019sptdc");
//        load(Conference.HYDRA, LocalDate.of(2019, 7, 11), "2019hydra");
//        load(Conference.TECH_TRAIN,LocalDate.of(2019, 8, 24), "2019tt");
//        load(Conference.JOKER, LocalDate.of(2019, 10, 25), "2019joker");
//        load(Conference.DEV_OOPS, LocalDate.of(2019, 10, 29), "2019devoops");
//        load(Conference.CPP_RUSSIA, LocalDate.of(2019, 10, 31), "2019-spb-cpp");
//        load(Conference.DOT_NEXT, LocalDate.of(2019, 11, 6), "2019msk");
//        load(Conference.HOLY_JS, LocalDate.of(2019, 11, 8), "2019msk");
//        load(Conference.HEISENBUG, LocalDate.of(2019, 12, 5), "2019msk");
//        load(Conference.MOBIUS, LocalDate.of(2019, 12, 7), "2019msk");

        // 2020
//        load(Conference.DOT_NEXT, LocalDate.of(2020, 4, 6), "2020-spb");
//        load(Conference.HEISENBUG, LocalDate.of(2020, 4, 8), "2020-spb");
//        load(Conference.HOLY_JS, LocalDate.of(2020, 4, 10), "2020-spb");
//        load(Conference.CPP_RUSSIA, LocalDate.of(2020, 4, 27), "2020-msk-cpp");
//        load(Conference.DEV_OOPS, LocalDate.of(2020, 4, 29), "2020-msk-devoops");
//        load(Conference.JPOINT, LocalDate.of(2020, 5, 15), "2020-jpoint");
//        load(Conference.MOBIUS, LocalDate.of(2020, 6, 23), "2020-spb");
//        load(Conference.SPTDC, LocalDate.of(2020, 7, 8), "2020-spb-sptdc");         // valid date?
//        load(Conference.HYDRA, LocalDate.of(2020, 7, 11), "2020-spb-hydra");        // valid date?
//        load(Conference.TECH_TRAIN,LocalDate.of(2020, 8, 24), "2020-techtrain");    // valid date?
//        load(Conference.DEV_OOPS, LocalDate.of(2020, 10, 7), "2020-spb-devoops");
//        load(Conference.CPP_RUSSIA, LocalDate.of(2020, 10, 9), "2020-spb-cpp");
//        load(Conference.JOKER, LocalDate.of(2020, 10, 23), "2020-joker");
//        load(Conference.HEISENBUG, LocalDate.of(2020, 11, 3), "2020-msk");
//        load(Conference.DOT_NEXT, LocalDate.of(2020, 11, 5), "2020-msk");
//        load(Conference.HOLY_JS, LocalDate.of(2020, 11, 8), "2020-msk");
//        load(Conference.MOBIUS, LocalDate.of(2020, 12, 7), "2020-msk");             // valid date?
    }
}
