package guess.dao;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.source.*;
import guess.util.yaml.YamlUtils;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Place, event type, event, speaker, talk DAO  implementation.
 */
@Repository
public class PlaceEventTypeEventSpeakerTalkDaoImpl implements PlaceDao, EventTypeDao, EventDao, SpeakerDao, TalkDao {
    private final SourceInformation sourceInformation;

    public PlaceEventTypeEventSpeakerTalkDaoImpl() throws IOException, SpeakerDuplicatedException {
        this.sourceInformation = YamlUtils.readSourceInformation();
    }

    @Override
    public List<Event> getEvents() {
        return sourceInformation.getEvents();
    }

    @Override
    public Event getEventById(long id) {
        return sourceInformation.getEvents().stream()
                .filter(e -> (e.getId() == id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Event> getEvents(long eventTypeId) {
        return sourceInformation.getEvents().stream()
                .filter(e -> (e.getEventTypeId() == eventTypeId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> getEventsFromDate(LocalDate date) {
        return sourceInformation.getEvents().stream()
                .filter(e -> !date.isAfter(e.getEndDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventType> getEventTypes() {
        return sourceInformation.getEventTypes();
    }

    @Override
    public EventType getEventTypeById(long id) {
        return sourceInformation.getEventTypes().stream()
                .filter(et -> (et.getId() == id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Place> getPlaces() {
        return sourceInformation.getPlaces();
    }

    @Override
    public List<Speaker> getSpeakers() {
        return sourceInformation.getSpeakers();
    }

    @Override
    public List<Talk> getTalks() {
        return sourceInformation.getTalks();
    }
}
