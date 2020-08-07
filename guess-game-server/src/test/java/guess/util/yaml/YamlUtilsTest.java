package guess.util.yaml;

import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.*;

@RunWith(Enclosed.class)
public class YamlUtilsTest {
    @RunWith(Parameterized.class)
    public static class LinkSpeakersToTalksTest {
        @Parameters
        public static Collection<Object[]> data() {
            Talk talk = new Talk();
            talk.setSpeakerIds(Collections.emptyList());

            return Arrays.asList(new Object[][]{
                    {Collections.emptyMap(), List.of(talk)}
            });
        }

        private final Map<Long, Speaker> speakers;
        private final List<Talk> talks;

        public LinkSpeakersToTalksTest(Map<Long, Speaker> speakers, List<Talk> talks) {
            this.speakers = speakers;
            this.talks = talks;
        }

        @Test(expected = IllegalStateException.class)
        public void linkSpeakersToTalks() {
            YamlUtils.linkSpeakersToTalks(speakers, talks);
        }
    }
}
