package guess.util.yaml;

import guess.domain.source.Event;
import guess.domain.source.Talk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class EventComparatorTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("compare method tests")
    class CompareTest {
        private Stream<Arguments> data() {
            LocalDate START_DATE0 = LocalDate.of(2021, 3, 29);
            LocalDate START_DATE1 = LocalDate.of(2021, 3, 30);
            LocalDate START_DATE2 = LocalDate.of(2021, 3, 28);

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

            Talk talk0 = new Talk();
            talk0.setTalkDay(1L);
            talk0.setTrackTime(LocalTime.of(10, 30));

            Event event6 = new Event();
            event6.setStartDate(START_DATE0);
            event6.setTalks(List.of(talk0));

            Event event7 = new Event();
            event7.setStartDate(START_DATE0);
            event7.setTalks(List.of(talk0));

            Talk talk1 = new Talk();
            talk1.setTalkDay(1L);
            talk1.setTrackTime(LocalTime.of(10, 45));

            Event event8 = new Event();
            event8.setStartDate(START_DATE0);
            event8.setTalks(List.of(talk1));

            return Stream.of(
                    arguments(null, null, 0),
                    arguments(null, event0, -1),
                    arguments(event0, null, 1),
                    arguments(event0, event1, 0),
                    arguments(event1, event2, -1),
                    arguments(event2, event1, 1),
                    arguments(event2, event3, -1),
                    arguments(event2, event4, 1),
                    arguments(event2, event5, 0),
                    arguments(event2, event6, -1),
                    arguments(event6, event2, 1),
                    arguments(event6, event7, 0),
                    arguments(event6, event8, -1),
                    arguments(event8, event6, 1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void compare(Event event1, Event event2, int expected) {
            assertEquals(expected, new EventComparator().compare(event1, event2));
        }
    }
}
