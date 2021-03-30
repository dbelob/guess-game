package guess.util.tagcloud;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.util.FileUtils;
import guess.util.LocalizationUtils;
import guess.util.yaml.YamlUtils;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static guess.util.tagcloud.TagCloudExporter.OUTPUT_DIRECTORY_NAME;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TagCloudExporter class tests")
class TagCloudExporterTest {
    private static final Conference JPOINT_CONFERENCE;
    private static final LocalDate EVENT_DATE;

    private static Place place0;

    private static Organizer organizer0;

    private static EventType eventType0;

    private static Talk talk0;
    private static Talk talk1;

    static {
        JPOINT_CONFERENCE = Conference.JPOINT;
        EVENT_DATE = LocalDate.of(2020, 6, 29);
    }

    @BeforeAll
    static void init() {
        place0 = new Place();

        organizer0 = new Organizer();

        Event event0 = new Event();
        event0.setId(0);
        event0.setStartDate(EVENT_DATE);
        event0.setPlace(place0);

        eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(JPOINT_CONFERENCE);
        eventType0.setOrganizer(organizer0);
        eventType0.setEvents(List.of(event0));

        Speaker speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Firstname Lastname")));

        Speaker speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Firstname Lastname")));

        talk0 = new Talk();
        talk0.setId(0);
        talk0.setLanguage(Language.ENGLISH.getCode());
        talk0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
        talk0.setShortDescription(List.of(new LocaleItem(Language.ENGLISH.getCode(), "ShortDescription0")));
        talk0.setSpeakers(List.of(speaker0));

        talk1 = new Talk();
        talk1.setId(1);
        talk1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));
        talk1.setLongDescription(List.of(new LocaleItem(Language.ENGLISH.getCode(), "LongDescription1")));
        talk1.setSpeakers(List.of(speaker0, speaker1));

        event0.setTalks(List.of(talk0, talk1));
    }

    @BeforeEach
    void setUp() throws IOException {
        FileUtils.deleteDirectory(OUTPUT_DIRECTORY_NAME);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(OUTPUT_DIRECTORY_NAME);
    }

    @Test
    void exportAllEvents() {
        SourceInformation sourceInformation = new SourceInformation(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                new SourceInformation.SpeakerInformation(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                ),
                List.of(talk0, talk1));

        new MockUp<YamlUtils>() {
            @Mock
            SourceInformation readSourceInformation() throws SpeakerDuplicatedException, IOException {
                return sourceInformation;
            }
        };

        new MockUp<TagCloudExporter>() {
            @Mock
            void export(List<Talk> talks, boolean isSaveTalkFiles, String commonFileName) throws IOException {
                // Nothing
            }
        };

        assertDoesNotThrow(TagCloudExporter::exportAllEvents);
    }

    @Test
    void exportTalksAndConference() {
        SourceInformation sourceInformation = new SourceInformation(
                List.of(place0),
                List.of(organizer0),
                List.of(eventType0),
                Collections.emptyList(),
                new SourceInformation.SpeakerInformation(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                ),
                Collections.emptyList());

        new MockUp<YamlUtils>() {
            @Mock
            SourceInformation readSourceInformation() throws SpeakerDuplicatedException, IOException {
                return sourceInformation;
            }
        };

        new MockUp<LocalizationUtils>() {
            @Mock
            String getString(List<LocaleItem> localeItems, Language language) {
                return "";
            }
        };

        new MockUp<TagCloudExporter>() {
            @Mock
            void export(List<Talk> talks, boolean isSaveTalkFiles, String commonFileName) throws IOException {
                // Nothing
            }
        };

        assertDoesNotThrow(() -> TagCloudExporter.exportTalksAndConference(JPOINT_CONFERENCE, EVENT_DATE));
    }

    @Test
    void export() {
        new MockUp<FileUtils>() {
            @Mock
            void deleteDirectory(String directoryName) throws IOException {
                // Nothing
            }
        };

        new MockUp<TagCloudUtils>() {
            @Mock
            String getTalkText(Talk talk) {
                return "";
            }
        };

        new MockUp<TagCloudExporter>() {
            @Mock
            void save(String text, String filename) throws IOException {
                // Nothing
            }
        };

        assertDoesNotThrow(() -> TagCloudExporter.export(List.of(talk0, talk1), true, "fileName.txt"));
    }

    @Test
    void save() throws IOException {
        Path directoryPath = Path.of(OUTPUT_DIRECTORY_NAME);
        Path filePath = Path.of(OUTPUT_DIRECTORY_NAME + "/talk00.txt");

        assertFalse(Files.exists(filePath));
        assertFalse(Files.exists(directoryPath));

        TagCloudExporter.save("content", "talk00.txt");

        assertTrue(Files.exists(directoryPath) && Files.isDirectory(directoryPath));
        assertTrue(Files.exists(filePath) && !Files.isDirectory(filePath));
    }

    @Test
    void main() {
        assertDoesNotThrow(() -> TagCloudExporter.main(new String[]{}));
    }
}
