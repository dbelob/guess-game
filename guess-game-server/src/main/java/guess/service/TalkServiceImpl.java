package guess.service;

import guess.dao.TalkDao;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Talk service implementation.
 */
@Service
public class TalkServiceImpl implements TalkService {
    private final TalkDao talkDao;

    @Autowired
    public TalkServiceImpl(TalkDao talkDao) {
        this.talkDao = talkDao;
    }

    @Override
    public List<Talk> getTalksBySpeaker(Speaker speaker) {
        return talkDao.getTalksBySpeaker(speaker);
    }
}
