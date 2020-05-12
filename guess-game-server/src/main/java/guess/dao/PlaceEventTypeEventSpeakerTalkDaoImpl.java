package guess.dao;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.source.*;
import guess.util.yaml.YamlUtils;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

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
    public List<EventType> getEventTypes() {
        return sourceInformation.getEventTypes();
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
