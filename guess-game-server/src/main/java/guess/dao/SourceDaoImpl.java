package guess.dao;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.source.*;
import guess.util.yaml.YamlUtils;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

/**
 * Source DAO implementation.
 */
@Repository
public class SourceDaoImpl implements SourceDao {
    private final SourceInformation sourceInformation;

    public SourceDaoImpl() throws IOException, SpeakerDuplicatedException {
        this.sourceInformation = YamlUtils.readSourceInformation();
    }

    SourceDaoImpl(SourceInformation sourceInformation) {
        this.sourceInformation = sourceInformation;
    }

    @Override
    public List<Place> getPlaces() {
        return sourceInformation.getPlaces();
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
                .orElseThrow();
    }

    @Override
    public EventType getEventTypeByEvent(Event event) {
        return sourceInformation.getEventTypes().stream()
                .filter(et -> et.getEvents().stream()
                        .anyMatch(e -> e.equals(event)))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public <T> List<T> getItemsByEventTypeIds(List<Long> eventTypeIds,
                                              LongFunction<List<T>> eventTypeConferenceFunction,
                                              Function<Void, List<T>> resultFunction) {
        if (eventTypeIds.isEmpty()) {
            return Collections.emptyList();
        } else {
            if (eventTypeIds.size() == 1) {
                Long eventTypeId = eventTypeIds.get(0);

                if (eventTypeId == null) {
                    return Collections.emptyList();
                }

                EventType eventType = getEventTypeById(eventTypeId);

                if (eventType == null) {
                    return Collections.emptyList();
                }

                if (eventType.isEventTypeConference()) {
                    return eventTypeConferenceFunction.apply(eventTypeId);
                }
            }

            return resultFunction.apply(null);
        }
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
                .orElseThrow();
    }

    @Override
    public List<Event> getEventsByEventTypeId(long eventTypeId) {
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
    public Event getEventByTalk(Talk talk) {
        return sourceInformation.getEvents().stream()
                .filter(e -> e.getTalks().stream()
                        .anyMatch(t -> t.equals(talk)))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public List<Speaker> getSpeakers() {
        return sourceInformation.getSpeakers();
    }

    @Override
    public Speaker getSpeakerById(long id) {
        return sourceInformation.getSpeakers().stream()
                .filter(s -> (s.getId() == id))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public List<Talk> getTalks() {
        return sourceInformation.getTalks();
    }

    @Override
    public Talk getTalkById(long id) {
        return sourceInformation.getTalks().stream()
                .filter(t -> (t.getId() == id))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public List<Talk> getTalksBySpeaker(Speaker speaker) {
        return sourceInformation.getTalks().stream()
                .filter(t -> (t.getSpeakers().stream()
                        .anyMatch(s -> s.equals(speaker))))
                .collect(Collectors.toList());
    }
}
