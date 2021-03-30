package guess.util.tagcloud;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.util.FileUtils;
import guess.util.LocalizationUtils;
import guess.util.yaml.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Tag cloud exporter.
 */
public class TagCloudExporter {
    private static final Logger log = LoggerFactory.getLogger(TagCloudExporter.class);

    static final String OUTPUT_DIRECTORY_NAME = "output";

    private TagCloudExporter() {
    }

    static void exportAllEvents() throws IOException, SpeakerDuplicatedException {
        // Read event types, places, events, companies, speakers, talks from resource files
        SourceInformation resourceSourceInformation = YamlUtils.readSourceInformation();

        export(resourceSourceInformation.getTalks(), false, "allEvents.txt");
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

        export(resourceEvent.getTalks(), true, String.format("event%d.txt", resourceEvent.getId()));
    }

    static void export(List<Talk> talks, boolean isSaveTalkFiles, String commonFileName) throws IOException {
        StringBuilder conferenceSb = new StringBuilder();
        Set<String> talkStopWords = new TreeSet<>();

        FileUtils.deleteDirectory(OUTPUT_DIRECTORY_NAME);

        for (Talk talk : talks) {
            String talkText = TagCloudUtils.getTalkText(talk);

            conferenceSb.append(talkText);

            if (isSaveTalkFiles) {
                save(talkText, String.format("talk%04d.txt", talk.getId()));
            }

            // Talk stop words
            if (talk.getSpeakers().size() == 1) {
                Speaker speaker = talk.getSpeakers().get(0);

                speaker.getName().stream()
                        .map(LocaleItem::getText)
                        .map(name -> name.toLowerCase().split(" "))
                        .map(Arrays::asList)
                        .forEach(talkStopWords::addAll);
            }
        }

        save(conferenceSb.toString(), commonFileName);
        save(String.join("\n", talkStopWords), "speaker-stop-words.txt");
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
//        exportAllEvents();
//        exportTalksAndConference(Conference.JOKER, LocalDate.of(2020, 11, 25));
    }
}
