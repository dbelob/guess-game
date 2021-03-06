package guess.util;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.util.yaml.YamlUtils;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static guess.util.TagCloudExporter.OUTPUT_DIRECTORY_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TagCloudExporterTest {
    @BeforeEach
    void setUp() throws IOException {
        FileUtils.deleteDirectory(OUTPUT_DIRECTORY_NAME);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(OUTPUT_DIRECTORY_NAME);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("exportTalksAndConference method tests")
    class ExportTalksAndConferenceTest {
        private Stream<Arguments> data() {
            final Conference JPOINT_CONFERENCE = Conference.JPOINT;
            final LocalDate EVENT_DATE = LocalDate.of(2020, 6, 29);

            Place place0 = new Place();

            Organizer organizer0 = new Organizer();

            Event event0 = new Event();
            event0.setId(0);
            event0.setStartDate(EVENT_DATE);
            event0.setPlace(place0);

            EventType eventType0 = new EventType();
            eventType0.setId(0);
            eventType0.setConference(JPOINT_CONFERENCE);
            eventType0.setOrganizer(organizer0);
            eventType0.setEvents(List.of(event0));

            Talk talk0 = new Talk();
            talk0.setId(0);
            talk0.setLanguage(Language.ENGLISH.getCode());
            talk0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name0")));
            talk0.setShortDescription(List.of(new LocaleItem(Language.ENGLISH.getCode(), "ShortDescription0")));

            Talk talk1 = new Talk();
            talk1.setId(1);
            talk1.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name1")));
            talk1.setLongDescription(List.of(new LocaleItem(Language.ENGLISH.getCode(), "LongDescription1")));

            event0.setTalks(List.of(talk0, talk1));

            return Stream.of(
                    arguments(JPOINT_CONFERENCE, EVENT_DATE,
                            new SourceInformation(
                                    List.of(place0),
                                    List.of(organizer0),
                                    List.of(eventType0),
                                    Collections.emptyList(),
                                    new SourceInformation.SpeakerInformation(
                                            Collections.emptyList(),
                                            Collections.emptyList(),
                                            Collections.emptyList()
                                    ),
                                    Collections.emptyList())
                    )
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void exportTalksAndConference(Conference conference, LocalDate startDate, SourceInformation sourceInformation) {
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
                void save(String text, String filename) throws IOException {
                    // Nothing
                }
            };

            assertDoesNotThrow(() -> TagCloudExporter.exportTalksAndConference(conference, startDate));
        }
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
