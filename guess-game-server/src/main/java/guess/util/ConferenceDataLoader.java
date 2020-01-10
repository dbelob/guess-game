package guess.util;

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

    private static void load(ContentfulUtils.ConferenceSpaceInfo conferenceSpaceInfo, String conferenceCode) {
        List<Talk> talks = ContentfulUtils.getTalks(conferenceSpaceInfo.getSpaceId(), conferenceSpaceInfo.getAccessToken(), conferenceCode);
        log.info("Talks: {}, {}", talks.size(), talks);
        talks.forEach(
                t -> log.info("Talk: name: {}, nameEn: {}",
                        LocalizationUtils.getString(t.getName(), Language.ENGLISH),
                        LocalizationUtils.getString(t.getName(), Language.RUSSIAN))
        );
    }

    public static void main(String[] args) {
        ContentfulUtils.ConferenceSpaceInfo conferenceSpaceInfo = ContentfulUtils.ConferenceSpaceInfo.COMMON_SPACE_INFO;

        load(conferenceSpaceInfo, "2019-spb-cpp");
    }
}
