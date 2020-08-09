package guess.util.yaml;

import guess.domain.source.*;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.*;
import java.util.function.Function;

import static junit.framework.TestCase.assertEquals;

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

    @RunWith(Parameterized.class)
    public static class LinkEventsToEventTypesTest {
        @Parameters
        public static Collection<Object[]> data() {
            Event event0 = new Event();
            event0.setEventTypeId(0);

            Event event1 = new Event();
            event1.setEventTypeId(1);

            EventType eventType0 = new EventType();
            eventType0.setId(0);

            return Arrays.asList(new Object[][]{
                    {Collections.emptyMap(), List.of(event0)},
                    {Map.of(0L, eventType0), List.of(event1)},
                    {Map.of(0L, eventType0), List.of(event0, event1)},
                    {Map.of(0L, eventType0), List.of(event1, event0)}
            });
        }

        private final Map<Long, EventType> eventTypes;
        private final List<Event> events;

        public LinkEventsToEventTypesTest(Map<Long, EventType> eventTypes, List<Event> events) {
            this.eventTypes = eventTypes;
            this.events = events;
        }

        @Test(expected = NullPointerException.class)
        public void linkSpeakersToTalks() {
            YamlUtils.linkEventsToEventTypes(eventTypes, events);
        }
    }

    @RunWith(Parameterized.class)
    public static class LinkEventsToPlacesTest {
        @Parameters
        public static Collection<Object[]> data() {
            Event event0 = new Event();
            event0.setPlaceId(0);

            Event event1 = new Event();
            event1.setPlaceId(1);

            Place place0 = new Place();
            place0.setId(0);

            return Arrays.asList(new Object[][]{
                    {Collections.emptyMap(), List.of(event0)},
                    {Map.of(0L, place0), List.of(event1)},
                    {Map.of(0L, place0), List.of(event0, event1)},
                    {Map.of(0L, place0), List.of(event1, event0)}
            });
        }

        private final Map<Long, Place> places;
        private final List<Event> events;

        public LinkEventsToPlacesTest(Map<Long, Place> places, List<Event> events) {
            this.places = places;
            this.events = events;
        }

        @Test(expected = NullPointerException.class)
        public void linkSpeakersToTalks() {
            YamlUtils.linkEventsToPlaces(places, events);
        }
    }

    @RunWith(Parameterized.class)
    public static class LinkTalksToEventsTest {
        @Parameters
        public static Collection<Object[]> data() {
            Event event0 = new Event();
            event0.setPlaceId(0);
            event0.setTalkIds(List.of(0L));

            Event event1 = new Event();
            event1.setPlaceId(1);
            event1.setTalkIds(List.of(1L));

            Talk talk0 = new Talk();
            talk0.setId(0L);

            return Arrays.asList(new Object[][]{
                    {Collections.emptyMap(), List.of(event0)},
                    {Map.of(0L, talk0), List.of(event1)},
                    {Map.of(0L, talk0), List.of(event0, event1)},
                    {Map.of(0L, talk0), List.of(event1, event0)}
            });
        }

        private final Map<Long, Talk> talks;
        private final List<Event> events;

        public LinkTalksToEventsTest(Map<Long, Talk> talks, List<Event> events) {
            this.talks = talks;
            this.events = events;
        }

        @Test(expected = NullPointerException.class)
        public void linkSpeakersToTalks() {
            YamlUtils.linkTalksToEvents(talks, events);
        }
    }

    @RunWith(Parameterized.class)
    public static class ListToMapTest {
        @Parameters
        public static Collection<Object[]> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);

            Function<? super Speaker, ? extends Long> keyExtractor = (Function<Speaker, Long>) Speaker::getId;

            return Arrays.asList(new Object[][]{
                    {List.of(speaker0, speaker0), keyExtractor},
                    {List.of(speaker0, speaker1, speaker1), keyExtractor},
                    {List.of(speaker0, speaker1, speaker1, speaker0), keyExtractor}
            });
        }

        private final List<Speaker> speakers;
        private final Function<? super Speaker, ? extends Long> keyExtractor;

        public ListToMapTest(List<Speaker> speakers, Function<? super Speaker, ? extends Long> keyExtractor) {
            this.speakers = speakers;
            this.keyExtractor = keyExtractor;
        }

        @Test(expected = IllegalStateException.class)
        public void listToMap() {
            YamlUtils.listToMap(speakers, keyExtractor);
        }
    }

    @RunWith(Parameterized.class)
    public static class FindSpeakerDuplicatesTest {
        @Parameters
        public static Collection<Object[]> data() {
            Speaker speaker0 = new Speaker();
            speaker0.setId(0);
            speaker0.setName(List.of(new LocaleItem("en", "name0")));

            Speaker speaker1 = new Speaker();
            speaker1.setId(1);
            speaker1.setName(List.of(new LocaleItem("en", "name0")));
            speaker1.setCompany(List.of(new LocaleItem("en", null)));

            Speaker speaker2 = new Speaker();
            speaker2.setId(2);
            speaker2.setName(List.of(new LocaleItem("en", "name0")));
            speaker2.setCompany(List.of(new LocaleItem("en", "")));

            Speaker speaker3 = new Speaker();
            speaker3.setId(3);
            speaker3.setName(List.of(new LocaleItem("en", "name3")));
            speaker3.setCompany(List.of(new LocaleItem("en", "company3")));

            Speaker speaker4 = new Speaker();
            speaker4.setId(4);
            speaker4.setName(List.of(new LocaleItem("en", "name3")));
            speaker4.setCompany(List.of(new LocaleItem("en", "company3")));

            return Arrays.asList(new Object[][]{
                    {Collections.emptyList(), false},
                    {List.of(speaker0, speaker1), true},
                    {List.of(speaker0, speaker2), true},
                    {List.of(speaker0, speaker1, speaker2), true},
                    {List.of(speaker3, speaker4), true}
            });
        }

        private final List<Speaker> speakers;
        private final boolean expected;

        public FindSpeakerDuplicatesTest(List<Speaker> speakers, boolean expected) {
            this.speakers = speakers;
            this.expected = expected;
        }

        @Test
        public void listToMap() {
            assertEquals(expected, YamlUtils.findSpeakerDuplicates(speakers));
        }
    }
}
