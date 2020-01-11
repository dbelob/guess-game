package guess.util;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.Talk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Conference data loader.
 */
public class ConferenceDataLoader {
    private static final Logger log = LoggerFactory.getLogger(ConferenceDataLoader.class);

    private static void load(Conference conference, String conferenceCode) {
        List<Talk> talks = ContentfulUtils.getTalks(conference, conferenceCode);
        log.info("Talks: {}, {}", talks.size(), talks);
        talks.forEach(
                t -> log.info("Talk: name: {}, nameEn: {}",
                        LocalizationUtils.getString(t.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(t.getName(), Language.RUSSIAN))
        );
    }

    public static void main(String[] args) {
        load(Conference.CPP_RUSSIA, "2019-spb-cpp");
    }
}
