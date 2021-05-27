package guess.util.yaml;

import guess.domain.source.Event;
import guess.domain.source.Talk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class EventComparatorTest {
    private static int extractSign(int value) {
        if (value < 0) {
            return -1;
        }

        return (value == 0) ? 0 : 1;
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("compare method tests")
    class CompareTest {
        private Stream<Arguments> data() {
            final int COMPARE_START_DATE_RESULT = 42;

            Event event0 = new Event();
            Event event1 = new Event();

            return Stream.of(
                    arguments(null, null, COMPARE_START_DATE_RESULT, 0),
                    arguments(null, event0, COMPARE_START_DATE_RESULT, -1),
                    arguments(event0, null, COMPARE_START_DATE_RESULT, 1),
                    arguments(event0, event1, COMPARE_START_DATE_RESULT, 1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void compare(Event event1, Event event2, int compareStartDateResult, int expected) {
            try (MockedStatic<EventComparator> mockedStatic = Mockito.mockStatic(EventComparator.class)) {
                mockedStatic.when(() -> EventComparator.compareStartDate(event1, event2))
                        .thenReturn(compareStartDateResult);

                int actual = extractSign(new EventComparator().compare(event1, event2));

                assertEquals(expected, actual);
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("compareStartDate method tests")
    class CompareStartDateTest {
        private Stream<Arguments> data() {
            final int COMPARE_TRACK_TIME_RESULT = 42;
            LocalDate START_DATE0 = LocalDate.of(2021, 3, 29);
            LocalDate START_DATE1 = LocalDate.of(2021, 3, 31);
            LocalDate START_DATE2 = LocalDate.of(2021, 3, 27);

            Event event0 = new Event();

            Event event1 = new Event();

            Event event2 = new Event();
            event2.setStartDate(START_DATE0);

            Event event3 = new Event();
            event3.setStartDate(START_DATE1);

            Event event4 = new Event();
            event4.setStartDate(START_DATE2);

            Event event5 = new Event();
            event5.setStartDate(START_DATE0);

            return Stream.of(
                    arguments(event0, event1, COMPARE_TRACK_TIME_RESULT, 0),
                    arguments(event1, event2, COMPARE_TRACK_TIME_RESULT, -1),
                    arguments(event2, event1, COMPARE_TRACK_TIME_RESULT, 1),
                    arguments(event2, event3, COMPARE_TRACK_TIME_RESULT, -1),
                    arguments(event2, event4, COMPARE_TRACK_TIME_RESULT, 1),
                    arguments(event2, event5, COMPARE_TRACK_TIME_RESULT, 1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void compareStartDate(Event event1, Event event2, int compareTrackTimeResult, int expected) {
            try (MockedStatic<EventComparator> mockedStatic = Mockito.mockStatic(EventComparator.class, Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(() -> EventComparator.compareTrackTime(event1, event2))
                        .thenReturn(compareTrackTimeResult);

                int actual = extractSign(EventComparator.compareStartDate(event1, event2));

                assertEquals(expected, actual);
            }
        }
    }

    @Test
    void getFirstTrackTime() {
        Talk talk0 = new Talk();

        Talk talk1 = new Talk();
        talk1.setTalkDay(1L);

        Talk talk2 = new Talk();
        talk2.setTrackTime(LocalTime.of(10, 0));

        Talk talk3 = new Talk();
        talk3.setTalkDay(1L);
        talk3.setTrackTime(LocalTime.of(10, 30));

        Talk talk4 = new Talk();
        talk4.setTalkDay(1L);
        talk4.setTrackTime(LocalTime.of(10, 45));

        Talk talk5 = new Talk();
        talk5.setTalkDay(2L);
        talk5.setTrackTime(LocalTime.of(9, 0));

        List<Talk> talks = List.of(talk0, talk1, talk2, talk3, talk4);

        LocalTime expected = LocalTime.of(10, 30);
        Optional<LocalTime> actual = EventComparator.getFirstTrackTime(talks);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("compareTrackTime method tests")
    class CompareTrackTimeTest {
        private Stream<Arguments> data() {
            Event event0 = new Event();

            Event event1 = new Event();

            Talk talk0 = new Talk();
            talk0.setTalkDay(1L);
            talk0.setTrackTime(LocalTime.of(10, 30));

            Event event2 = new Event();
            event2.setTalks(List.of(talk0));

            Event event3 = new Event();
            event3.setTalks(List.of(talk0));

            Talk talk1 = new Talk();
            talk1.setTalkDay(1L);
            talk1.setTrackTime(LocalTime.of(10, 45));

            Event event4 = new Event();
            event4.setTalks(List.of(talk1));

            return Stream.of(
                    arguments(event0, event1, 0),
                    arguments(event0, event2, -1),
                    arguments(event2, event0, 1),
                    arguments(event2, event3, 0),
                    arguments(event2, event4, -1),
                    arguments(event4, event2, 1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void compareTrackTime(Event event1, Event event2, int expected) {
            try (MockedStatic<EventComparator> mockedStatic = Mockito.mockStatic(EventComparator.class, Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(() -> EventComparator.getFirstTrackTime(Mockito.any()))
                        .thenAnswer(
                                (Answer<Optional<LocalTime>>) invocation -> {
                                    Object[] args = invocation.getArguments();
                                    List<Talk> talks = (List<Talk>) args[0];

                                    return talks.isEmpty() ? Optional.empty() : Optional.of(talks.get(0).getTrackTime());
                                }
                        );

                int actual = extractSign(EventComparator.compareTrackTime(event1, event2));

                assertEquals(expected, actual);
            }
        }
    }
}
