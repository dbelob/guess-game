package guess.util.yaml;

import guess.domain.source.Event;
import guess.domain.source.Talk;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Event comparator.
 */
public class EventComparator implements Comparator<Event> {
    @Override
    public int compare(Event event1, Event event2) {
        // Compare event
        if (event1 == null) {
            if (event2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (event2 == null) {
                return 1;
            } else {
                // Compare start date
                return compareStartDate(event1, event2);
            }
        }
    }

    static int compareStartDate(Event event1, Event event2) {
        LocalDate eventStartDate1 = event1.getStartDate();
        LocalDate eventStartDate2 = event2.getStartDate();

        if (eventStartDate1 == null) {
            if (eventStartDate2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (eventStartDate2 == null) {
                return 1;
            } else {
                // Compare track time
                if (eventStartDate1.isEqual(eventStartDate2)) {
                    return compareTrackTime(event1, event2);
                } else {
                    return eventStartDate1.compareTo(eventStartDate2);
                }
            }
        }
    }

    static Optional<LocalTime> getFirstTrackTime(List<Talk> talks) {
        return talks.stream()
                .filter(t -> (t.getTalkDay() != null) && (t.getTrackTime() != null))
                .sorted(Comparator.comparing(Talk::getTalkDay).thenComparing(Talk::getTrackTime))
                .map(Talk::getTrackTime)
                .findFirst();
    }

    static int compareTrackTime(Event event1, Event event2) {
        Optional<LocalTime> eventTrackTime1 = getFirstTrackTime(event1.getTalks());
        Optional<LocalTime> eventTrackTime2 = getFirstTrackTime(event2.getTalks());

        if (eventTrackTime1.isEmpty()) {
            if (eventTrackTime2.isEmpty()) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (eventTrackTime2.isEmpty()) {
                return 1;
            } else {
                if (eventTrackTime1.get().equals(eventTrackTime2.get())) {
                    return 0;
                } else {
                    return eventTrackTime1.get().compareTo(eventTrackTime2.get());
                }
            }
        }
    }
}
