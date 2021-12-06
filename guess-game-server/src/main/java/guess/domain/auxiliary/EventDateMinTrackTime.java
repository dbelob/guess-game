package guess.domain.auxiliary;

import guess.domain.source.Event;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Event, date, minimal track time.
 */
public record EventDateMinTrackTime(Event event, LocalDate date, LocalTime minTrackTime) {
}
