package guess.domain.source;

import java.util.List;

/**
 * Event type list.
 */
public class EventTypeList {
    private List<EventType> eventTypes;

    public EventTypeList() {
    }

    public EventTypeList(List<EventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public List<EventType> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<EventType> eventTypes) {
        this.eventTypes = eventTypes;
    }
}
