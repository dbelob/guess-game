package guess.dao;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.source.*;
import guess.util.yaml.YamlUtils;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    public List<Organizer> getOrganizers() {
        return sourceInformation.getOrganizers();
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
                .toList();
    }

    @Override
    public List<Event> getEventsFromDateTime(LocalDateTime dateTime) {
        return sourceInformation.getEvents().stream()
                .filter(e -> {
                    var zonedEndDateTime = ZonedDateTime.of(
                            e.getEndDate(),
                            LocalTime.of(0, 0, 0),
                            e.getFinalTimeZoneId());
                    ZonedDateTime zonedNextDayEndDateTime = zonedEndDateTime.plus(1, ChronoUnit.DAYS);
                    var eventUtcEndLocalDateTime = zonedNextDayEndDateTime
                            .withZoneSameInstant(ZoneId.of("UTC"))
                            .toLocalDateTime();

                    return dateTime.isBefore(eventUtcEndLocalDateTime);
                })
                .toList();
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
    public List<Company> getCompanies() {
        return sourceInformation.getCompanies();
    }

    @Override
    public Company getCompanyById(long id) {
        return sourceInformation.getCompanies().stream()
                .filter(c -> (c.getId() == id))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public List<Company> getCompaniesByIds(List<Long> ids) {
        return sourceInformation.getCompanies().stream()
                .filter(c -> (ids.contains(c.getId())))
                .toList();
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
    public List<Speaker> getSpeakerByIds(List<Long> ids) {
        return sourceInformation.getSpeakers().stream()
                .filter(s -> (ids.contains(s.getId())))
                .toList();
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
                .toList();
    }
}
