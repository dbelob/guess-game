package guess.domain.source;

import java.util.List;

/**
 * Event types.
 */
public class EventTypes {
    private List<EventType> eventTypes;

    public EventTypes() {
    }

    public EventTypes(List<EventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public List<EventType> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<EventType> eventTypes) {
        this.eventTypes = eventTypes;
    }
}
