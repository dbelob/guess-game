package guess.dao;

import guess.domain.source.Talk;

import java.util.List;

/**
 * Talk DAO.
 */
public interface TalkDao {
    List<Talk> getTalks();
}
