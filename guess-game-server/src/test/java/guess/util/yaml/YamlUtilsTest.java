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
    public static abstract class LinkSpeakersToTalksExceptionTest {
        protected final Map<Long, Speaker> speakers;
        protected final List<Talk> talks;

        public LinkSpeakersToTalksExceptionTest(Map<Long, Speaker> speakers, List<Talk> talks) {
            this.speakers = speakers;
            this.talks = talks;
        }
    }

    @RunWith(Parameterized.class)
    public static class LinkSpeakersToTalksIllegalStateExceptionTest extends LinkSpeakersToTalksExceptionTest {
        @Parameters
        public static Collection<Object[]> data() {
            Talk talk0 = new Talk();
            talk0.setSpeakerIds(Collections.emptyList());

            Talk talk1 = new Talk();
            talk1.setSpeakerIds(List.of(0L));

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            return Arrays.asList(new Object[][]{
                    {Collections.emptyMap(), List.of(talk0)},
                    {Collections.emptyMap(), List.of(talk0, talk1)},
                    {Map.of(0L, speaker0), List.of(talk1, talk0)},
            });
        }

        public LinkSpeakersToTalksIllegalStateExceptionTest(Map<Long, Speaker> speakers, List<Talk> talks) {
            super(speakers, talks);
        }

        @Test(expected = IllegalStateException.class)
        public void linkSpeakersToTalks() {
            YamlUtils.linkSpeakersToTalks(speakers, talks);
        }
    }

    @RunWith(Parameterized.class)
    public static class LinkSpeakersToTalksNullPointerExceptionTest extends LinkSpeakersToTalksExceptionTest {
        @Parameters
        public static Collection<Object[]> data() {
            Talk talk0 = new Talk();
            talk0.setSpeakerIds(Collections.emptyList());

            Talk talk1 = new Talk();
            talk1.setSpeakerIds(List.of(0L));

            Talk talk2 = new Talk();
            talk2.setSpeakerIds(List.of(1L));

            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            return Arrays.asList(new Object[][]{
                    {Collections.emptyMap(), List.of(talk1, talk0)},
                    {Map.of(0L, speaker0), List.of(talk1, talk2, talk0)}
            });
        }

        public LinkSpeakersToTalksNullPointerExceptionTest(Map<Long, Speaker> speakers, List<Talk> talks) {
            super(speakers, talks);
        }

        @Test(expected = NullPointerException.class)
        public void linkSpeakersToTalks() {
            YamlUtils.linkSpeakersToTalks(speakers, talks);
        }
    }
}
