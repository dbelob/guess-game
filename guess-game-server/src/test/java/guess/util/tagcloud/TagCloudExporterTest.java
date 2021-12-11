package guess.util.tagcloud;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.util.FileUtils;
import guess.util.LocalizationUtils;
import guess.util.yaml.YamlUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static guess.util.tagcloud.TagCloudExporter.OUTPUT_DIRECTORY_NAME;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
                        Collections.emptyList(),
                        Collections.emptyList()
                ),
                List.of(talk0, talk1));

        try (MockedStatic<TagCloudExporter> tagCloudExporterMockedStatic = Mockito.mockStatic(TagCloudExporter.class);
             MockedStatic<YamlUtils> yamlUtilsMockedStatic = Mockito.mockStatic(YamlUtils.class)) {
            tagCloudExporterMockedStatic.when(TagCloudExporter::exportAllEvents)
                    .thenCallRealMethod();
            yamlUtilsMockedStatic.when(YamlUtils::readSourceInformation)
                    .thenReturn(sourceInformation);

            assertDoesNotThrow(TagCloudExporter::exportAllEvents);
        }
    }

    @Test
    void exportTalksAndConference() throws SpeakerDuplicatedException, IOException {
        SourceInformation sourceInformation = new SourceInformation(
                List.of(place0),
                List.of(organizer0),
                List.of(eventType0),
                Collections.emptyList(),
                new SourceInformation.SpeakerInformation(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                ),
                Collections.emptyList());

        try (MockedStatic<TagCloudExporter> tagCloudExporterMockedStatic = Mockito.mockStatic(TagCloudExporter.class);
             MockedStatic<YamlUtils> yamlUtilsMockedStatic = Mockito.mockStatic(YamlUtils.class);
             MockedStatic<LocalizationUtils> localizationUtilsMockedStatic = Mockito.mockStatic(LocalizationUtils.class)) {
            tagCloudExporterMockedStatic.when(() -> TagCloudExporter.exportTalksAndConference(Mockito.any(Conference.class), Mockito.any(LocalDate.class)))
                    .thenCallRealMethod();
            yamlUtilsMockedStatic.when(YamlUtils::readSourceInformation)
                    .thenReturn(sourceInformation);
            localizationUtilsMockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.any(Language.class)))
                    .thenReturn("");

            assertDoesNotThrow(() -> TagCloudExporter.exportTalksAndConference(JPOINT_CONFERENCE, EVENT_DATE));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("export method tests")
    class ExportTest {
        private Stream<Arguments> data() {
            List<Talk> talks = List.of(talk0, talk1);

            return Stream.of(
                    arguments(talks, true, "fileName.txt"),
                    arguments(talks, false, "fileName.txt")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void export(List<Talk> talks, boolean isSaveTalkFiles, String commonFileName) {
            try (MockedStatic<TagCloudExporter> tagCloudExporterMockedStatic = Mockito.mockStatic(TagCloudExporter.class);
                 MockedStatic<FileUtils> fileUtilsMockedStatic = Mockito.mockStatic(FileUtils.class);
                 MockedStatic<TagCloudUtils> tagCloudUtilsMockedStatic = Mockito.mockStatic(TagCloudUtils.class)) {
                tagCloudExporterMockedStatic.when(() -> TagCloudExporter.export(Mockito.anyList(), Mockito.anyBoolean(), Mockito.anyString()))
                        .thenCallRealMethod();
                tagCloudUtilsMockedStatic.when(() -> TagCloudUtils.getTalkText(Mockito.any(Talk.class)))
                        .thenReturn("");

                assertDoesNotThrow(() -> TagCloudExporter.export(talks, isSaveTalkFiles, commonFileName));
            }
        }
    }

    @Test
    void save() {
        assertDoesNotThrow(() -> TagCloudExporter.save("content", "talk00.txt"));
    }

    @Test
    void main() {
        assertDoesNotThrow(() -> TagCloudExporter.main(new String[]{}));
    }
}
