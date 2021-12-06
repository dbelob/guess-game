package guess.domain.auxiliary;

import guess.domain.source.Event;

import java.time.LocalDateTime;

/**
 * Event, minimal track time, end date time.
 */
public record EventMinTrackTimeEndDayTime(Event event, LocalDateTime minTrackDateTime, LocalDateTime endDayDateTime) {
}
