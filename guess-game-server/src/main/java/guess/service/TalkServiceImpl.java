package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.dao.TalkDao;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.util.SearchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Talk service implementation.
 */
@Service
public class TalkServiceImpl implements TalkService {
    private final TalkDao talkDao;
    private final EventDao eventDao;
    private final EventTypeDao eventTypeDao;

    @Autowired
    public TalkServiceImpl(TalkDao talkDao, EventDao eventDao, EventTypeDao eventTypeDao) {
        this.talkDao = talkDao;
        this.eventDao = eventDao;
        this.eventTypeDao = eventTypeDao;
    }

    @Override
    public Talk getTalkById(long id) {
        return talkDao.getTalkById(id);
    }

    @Override
    public List<Talk> getTalks(Long eventTypeId, Long eventId, String talkName, String speakerName) {
        String trimmedLowerCasedTalkName = SearchUtils.trimAndLowerCase(talkName);
        String trimmedLowerCasedSpeakerName = SearchUtils.trimAndLowerCase(speakerName);
        boolean isTalkNameSet = SearchUtils.isStringSet(trimmedLowerCasedTalkName);
        boolean isSpeakerNameSet = SearchUtils.isStringSet(trimmedLowerCasedSpeakerName);

        if ((eventTypeId == null) && (eventId == null) && !isTalkNameSet && !isSpeakerNameSet) {
            return Collections.emptyList();
        } else {
            Stream<Talk> talkStream;
            if (eventTypeId != null) {
                if (eventId != null) {
                    talkStream = eventDao.getEventById(eventId).getTalks().stream();
                } else {
                    talkStream = eventTypeDao.getEventTypeById(eventTypeId).getEvents().stream()
                            .flatMap(e -> e.getTalks().stream());
                }
            } else {
                talkStream = talkDao.getTalks().stream();
            }

            return talkStream
                    .filter(t -> ((!isTalkNameSet || SearchUtils.isSubstringFound(trimmedLowerCasedTalkName, t.getName())) &&
                            (!isSpeakerNameSet || t.getSpeakers().stream()
                                    .anyMatch(s -> SearchUtils.isSubstringFound(trimmedLowerCasedSpeakerName, s.getName())))))
                    .toList();
        }
    }

    @Override
    public List<Talk> getTalksBySpeaker(Speaker speaker) {
        return talkDao.getTalksBySpeaker(speaker);
    }
}
