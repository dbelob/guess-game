package guess.util;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.SourceInformation;
import guess.domain.source.Talk;
import guess.util.yaml.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Tag cloud exporter.
 */
public class TagCloudExporter {
    private static final Logger log = LoggerFactory.getLogger(TagCloudExporter.class);

    static final String OUTPUT_DIRECTORY_NAME = "output";

    private TagCloudExporter() {
    }

    static void exportTalksAndConference(Conference conference, LocalDate startDate) throws IOException, SpeakerDuplicatedException {
        log.info("{} {}", conference, startDate);

        // Read event types, places, events, companies, speakers, talks from resource files
        SourceInformation resourceSourceInformation = YamlUtils.readSourceInformation();
        Optional<EventType> resourceOptionalEventType = resourceSourceInformation.getEventTypes().stream()
                .filter(et -> et.getConference().equals(conference))
                .findFirst();
        EventType resourceEventType = resourceOptionalEventType
                .orElseThrow(() -> new IllegalStateException(String.format("No event type found for conference %s (in resource files)", conference)));
        log.info("Event type (in resource files): nameEn: {}, nameRu: {}",
                LocalizationUtils.getString(resourceEventType.getName(), Language.ENGLISH),
                LocalizationUtils.getString(resourceEventType.getName(), Language.RUSSIAN));

        Optional<Event> resourceOptionalEvent = resourceOptionalEventType
                .flatMap(et -> et.getEvents().stream()
                        .filter(e -> e.getStartDate().equals(startDate))
                        .findFirst());
        Event resourceEvent = resourceOptionalEvent
                .orElseThrow(() -> new IllegalStateException(String.format("No event found for start date %s (in resource files)", startDate)));
        log.info("Event (in resource files): nameEn: {}, nameRu: {}, startDate: {}, endDate: {}",
                LocalizationUtils.getString(resourceEvent.getName(), Language.ENGLISH),
                LocalizationUtils.getString(resourceEvent.getName(), Language.RUSSIAN),
                resourceEvent.getStartDate(), resourceEvent.getEndDate());

        StringBuilder conferenceSb = new StringBuilder();

        FileUtils.deleteDirectory(OUTPUT_DIRECTORY_NAME);

        for (Talk talk : resourceEvent.getTalks()) {
            StringBuilder talkSb = new StringBuilder();
            Language language = Language.getLanguageByCode(talk.getLanguage());

            talkSb.append(LocalizationUtils.getString(talk.getName(), language));
            talkSb.append("\n");

            if (talk.getShortDescription() != null) {
                talkSb.append(LocalizationUtils.getString(talk.getShortDescription(), language));
                talkSb.append("\n");
            }

            if (talk.getLongDescription() != null) {
                talkSb.append(LocalizationUtils.getString(talk.getLongDescription(), language));
                talkSb.append("\n");
            }

            conferenceSb.append(talkSb);

            save(talkSb.toString(), String.format("talk%04d.txt", talk.getId()));
        }

        save(conferenceSb.toString(), String.format("event%d.txt", resourceEvent.getId()));
    }

    static void save(String text, String filename) throws IOException {
        File file = new File(String.format("%s/%s", OUTPUT_DIRECTORY_NAME, filename));
        FileUtils.checkAndCreateDirectory(file.getParentFile());

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(text);
        }

        log.info("File '{}' saved", file.getAbsolutePath());
    }

    public static void main(String[] args) throws IOException, SpeakerDuplicatedException {
//        exportTalksAndConference(Conference.JOKER, LocalDate.of(2020, 11, 25));
    }
}
