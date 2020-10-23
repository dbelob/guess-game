package guess.domain.statistics;

import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Talk;

import java.util.HashSet;
import java.util.Set;

/**
 * Internal speaker metrics.
 */
public class SpeakerMetricsInternal {
    private final Set<Talk> talks = new HashSet<>();
    private final Set<Event> events = new HashSet<>();
    private final Set<EventType> eventTypes = new HashSet<>();

    public Set<Talk> getTalks() {
        return talks;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public Set<EventType> getEventTypes() {
        return eventTypes;
    }
}
