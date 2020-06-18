package guess.service;

import guess.domain.source.Speaker;
import guess.domain.source.Talk;

import java.util.List;

/**
 * Talk service.
 */
public interface TalkService {
    List<Talk> getTalksBySpeaker(Speaker speaker);
}
