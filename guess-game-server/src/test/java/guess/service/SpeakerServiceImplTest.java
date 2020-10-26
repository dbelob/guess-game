package guess.service;

import guess.dao.SpeakerDao;
import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("SpeakerServiceImpl class tests")
@ExtendWith(SpringExtension.class)
class SpeakerServiceImplTest {
    private static Speaker speaker0;
    private static Speaker speaker1;
    private static Speaker speaker2;

    @BeforeAll
    static void init() {
        speaker0 = new Speaker();
        speaker0.setId(0);
        speaker0.setName(List.of(new LocaleItem(Language.ENGLISH.getCode(), "Name")));

        speaker1 = new Speaker();
        speaker1.setId(1);

        speaker2 = new Speaker();
        speaker2.setId(2);
    }

    @Test
    void getSpeakerById() {
        SpeakerDao speakerDao = Mockito.mock(SpeakerDao.class);
        SpeakerService speakerService = new SpeakerServiceImpl(speakerDao);

        Mockito.when(speakerDao.getSpeakerById(Mockito.anyLong())).thenReturn(speaker0);

        speakerService.getSpeakerById(0);
        Mockito.verify(speakerDao, VerificationModeFactory.times(1)).getSpeakerById(0);
        Mockito.verifyNoMoreInteractions(speakerDao);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakersByFirstLetter method tests")
    class GetSpeakersByFirstLetterTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, Collections.emptyList(), null, Collections.emptyList()),
                    arguments("n", Language.ENGLISH, List.of(speaker0), "", Collections.emptyList()),
                    arguments("n", Language.RUSSIAN, List.of(speaker0), "", Collections.emptyList()),
                    arguments("n", Language.ENGLISH, List.of(speaker0), " ", Collections.emptyList()),
                    arguments("n", Language.ENGLISH, List.of(speaker0), "N", List.of(speaker0))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakersByFirstLetter(String firstLetter, Language language, List<Speaker> speakers,
                                      String localizationString, List<Speaker> expected,
                                      @Mocked SpeakerDao speakerDaoMock) {
            new MockUp<LocalizationUtils>() {
                @Mock
                String getString(List<LocaleItem> localeItems, Language language) {
                    return Language.ENGLISH.equals(language) ? localizationString : null;
                }
            };

            new Expectations() {{
                speakerDaoMock.getSpeakers();
                result = speakers;
            }};

            SpeakerService speakerService = new SpeakerServiceImpl(speakerDaoMock);

            assertEquals(expected, speakerService.getSpeakersByFirstLetter(firstLetter, language));
        }
    }

    @Test
    void getSpeakers() {
        //TODO: implement
    }
}
