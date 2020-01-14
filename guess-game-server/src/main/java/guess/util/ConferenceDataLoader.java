package guess.util;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.SourceInformation;
import guess.domain.source.Talk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

        // Read talks from Contentful
        List<Talk> talks = ContentfulUtils.getTalks(conference, conferenceCode);
        log.info("Talks: {}", talks.size());
        talks.forEach(
                t -> log.info("Talk: nameEn: {}, name: {}",
                        LocalizationUtils.getString(t.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(t.getName(), Language.RUSSIAN))
        );
    }

    public static void main(String[] args) throws IOException, SpeakerDuplicatedException {
        // C++ Russia
        load(Conference.CPP_RUSSIA, LocalDate.of(2019, 4, 19), "2019cpp");
//        load(Conference.CPP_RUSSIA, LocalDate.of(2019, 10, 31), "2019-spb-cpp");
    }
}
