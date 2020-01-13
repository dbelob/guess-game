package guess.domain.source;

import java.util.List;

/**
 * Source information.
 */
public class SourceInformation {
    private final List<EventType> eventTypes;
    private final List<Event> events;
    private final List<Speaker> speakers;
    private final List<Talk> talks;

    public SourceInformation(List<EventType> eventTypes, List<Event> events, List<Speaker> speakers, List<Talk> talks) {
        this.eventTypes = eventTypes;
        this.events = events;
        this.speakers = speakers;
        this.talks = talks;
    }

    public List<EventType> getEventTypes() {
        return eventTypes;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public List<Talk> getTalks() {
        return talks;
    }
}
