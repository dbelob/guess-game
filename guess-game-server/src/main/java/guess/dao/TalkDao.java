package guess.dao;

import guess.domain.source.Speaker;
import guess.domain.source.Talk;

import java.util.List;

/**
 * Talk DAO.
 */
public interface TalkDao {
    List<Talk> getTalks();

    Talk getTalkById(long id);

    List<Talk> getTalksBySpeaker(Speaker speaker);
}
