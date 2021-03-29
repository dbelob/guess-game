package guess.util.yaml;

import guess.domain.source.Event;
import guess.domain.source.Talk;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
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
                LocalDate eventStartDate1 = event1.getStartDate();
                LocalDate eventStartDate2 = event2.getStartDate();

                // Compare start date
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
                            Optional<LocalTime> eventTrackTime1 = event1.getTalks().stream()
                                    .filter(t -> (t.getTalkDay() != null) && (t.getTrackTime() != null))
                                    .sorted(Comparator.comparing(Talk::getTalkDay).thenComparing(Talk::getTrackTime))
                                    .map(Talk::getTrackTime)
                                    .findFirst();

                            Optional<LocalTime> eventTrackTime2 = event2.getTalks().stream()
                                    .filter(t -> (t.getTalkDay() != null) && (t.getTrackTime() != null))
                                    .sorted(Comparator.comparing(Talk::getTalkDay).thenComparing(Talk::getTrackTime))
                                    .map(Talk::getTrackTime)
                                    .findFirst();

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
                                    return eventTrackTime1.get().isBefore(eventTrackTime2.get()) ? -1 : 1;
                                }
                            }
                        } else {
                            return eventStartDate1.isBefore(eventStartDate2) ? -1 : 1;
                        }
                    }
                }
            }
        }
    }
}
