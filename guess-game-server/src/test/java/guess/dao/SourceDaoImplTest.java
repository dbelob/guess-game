package guess.dao;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SourceDaoImpl class tests")
class SourceDaoImplTest {
    private static Place place0;
    private static Place place1;
    private static Place place2;

    private static Organizer organizer0;
    private static Organizer organizer1;

    private static EventType eventType0;
    private static EventType eventType1;
    private static EventType eventType2;

    private static Event event0;
    private static Event event1;
    private static Event event2;
    private static Event event3;

    private static Talk talk0;
    private static Talk talk1;
    private static Talk talk2;
    private static Talk talk3;

    private static Company company0;
    private static Company company1;
    private static Company company2;

    private static Speaker speaker0;
    private static Speaker speaker1;
    private static Speaker speaker2;
    private static Speaker speaker3;
    private static Speaker speaker4;
    private static Speaker speaker5;

    private static SourceDao sourceDao;

    @BeforeAll
    static void init() {
        place0 = new Place();
        place0.setId(0);

        place1 = new Place();
        place1.setId(1);

        place2 = new Place();
        place2.setId(2);

        organizer0 = new Organizer();
        organizer0.setId(0);

        organizer1 = new Organizer();
        organizer1.setId(1);

        eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);
        eventType0.setOrganizer(organizer0);
        eventType0.setTimeZoneId(ZoneId.of("Europe/Moscow"));

        eventType1 = new EventType();
        eventType1.setId(1);
        eventType1.setOrganizer(organizer0);
        eventType1.setTimeZoneId(ZoneId.of("Europe/Moscow"));

        eventType2 = new EventType();
        eventType2.setId(2);
        eventType2.setOrganizer(organizer1);
        eventType2.setTimeZoneId(ZoneId.of("Asia/Novosibirsk"));

        company0 = new Company();
        company0.setId(0);

        company1 = new Company();
        company1.setId(1);

        company2 = new Company();
        company2.setId(2);

        speaker0 = new Speaker();
        speaker0.setId(0);

        speaker1 = new Speaker();
        speaker1.setId(1);

        speaker2 = new Speaker();
        speaker2.setId(2);

        speaker3 = new Speaker();
        speaker3.setId(3);

        speaker4 = new Speaker();
        speaker4.setId(4);

        speaker5 = new Speaker();
        speaker5.setId(5);

        talk0 = new Talk();
        talk0.setId(0);
        talk0.setSpeakerIds(List.of(0L));
        talk0.setSpeakers(List.of(speaker0));

        talk1 = new Talk();
        talk1.setId(1);
        talk1.setSpeakerIds(List.of(1L, 2L));
        talk1.setSpeakers(List.of(speaker1, speaker2));

        talk2 = new Talk();
        talk2.setId(2);
        talk2.setSpeakerIds(List.of(2L, 2L));
        talk2.setSpeakers(List.of(speaker2, speaker3));

        talk3 = new Talk();
        talk3.setId(3);
        talk3.setSpeakerIds(List.of(4L));
        talk3.setSpeakers(List.of(speaker4));

        event0 = new Event();
        event0.setId(0);
        event0.setEventTypeId(eventType0.getId());
        event0.setEventType(eventType0);
        eventType0.setEvents(List.of(event0));
        event0.setStartDate(LocalDate.of(2020, 1, 1));
        event0.setEndDate(LocalDate.of(2020, 1, 2));
        event0.setTalkIds(List.of(0L));
        event0.setTalks(List.of(talk0));
        event0.setTimeZoneId(ZoneId.of("Europe/Moscow"));

        event1 = new Event();
        event1.setId(1);
        event1.setEventTypeId(eventType1.getId());
        event1.setEventType(eventType1);
        eventType1.setEvents(List.of(event1));
        event1.setStartDate(LocalDate.of(2020, 2, 1));
        event1.setEndDate(LocalDate.of(2020, 2, 2));
        event1.setTalkIds(List.of(1L));
        event1.setTalks(List.of(talk1));

        event2 = new Event();
        event2.setId(2);
        event2.setEventTypeId(eventType2.getId());
        event2.setEventType(eventType2);
        eventType2.setEvents(List.of(event2));
        event2.setStartDate(LocalDate.of(2020, 3, 1));
        event2.setEndDate(LocalDate.of(2020, 3, 2));
        event2.setTalkIds(List.of(2L));
        event2.setTalks(List.of(talk2));

        event3 = new Event();
        event3.setId(3);
        event3.setStartDate(LocalDate.of(2020, 4, 1));
        event3.setEndDate(LocalDate.of(2020, 4, 2));

        SourceInformation sourceInformation = new SourceInformation(
                List.of(place0, place1, place2),
                List.of(organizer0, organizer1),
                List.of(eventType0, eventType1, eventType2),
                List.of(event0, event1, event2),
                new SourceInformation.SpeakerInformation(
                        List.of(company0, company1, company2),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        List.of(speaker0, speaker1, speaker2, speaker3)
                ),
                List.of(talk0, talk1, talk2));
        sourceDao = new SourceDaoImpl(sourceInformation);
    }

    @Test
    void getPlaces() {
        assertEquals(List.of(place0, place1, place2), sourceDao.getPlaces());
    }

    @Test
    void getOrganizers() {
        assertEquals(List.of(organizer0, organizer1), sourceDao.getOrganizers());
    }

    @Test
    void getEventTypes() {
        assertEquals(List.of(eventType0, eventType1, eventType2), sourceDao.getEventTypes());
    }

    @Test
    void getEventTypeById() {
        assertEquals(eventType0, sourceDao.getEventTypeById(0));
        assertEquals(eventType1, sourceDao.getEventTypeById(1));
        assertEquals(eventType2, sourceDao.getEventTypeById(2));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getEventTypeById(3));
    }

    @Test
    void getEventTypeByEvent() {
        assertEquals(eventType0, sourceDao.getEventTypeByEvent(event0));
        assertEquals(eventType1, sourceDao.getEventTypeByEvent(event1));
        assertEquals(eventType2, sourceDao.getEventTypeByEvent(event2));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getEventTypeByEvent(event3));
    }

    @Test
    void getEvents() {
        assertEquals(List.of(event0, event1, event2), sourceDao.getEvents());
    }

    @Test
    void getEventById() {
        assertEquals(event0, sourceDao.getEventById(0));
        assertEquals(event1, sourceDao.getEventById(1));
        assertEquals(event2, sourceDao.getEventById(2));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getEventById(3));
    }

    @Test
    void getEventsByEventTypeId() {
        assertEquals(List.of(event0), sourceDao.getEventsByEventTypeId(0));
        assertEquals(List.of(event1), sourceDao.getEventsByEventTypeId(1));
        assertEquals(List.of(event2), sourceDao.getEventsByEventTypeId(2));
        assertEquals(Collections.emptyList(), sourceDao.getEventsByEventTypeId(3));
    }

    @Test
    void getEventsFromDateTime() {
        assertEquals(List.of(event0, event1, event2), sourceDao.getEventsFromDateTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0)));
        assertEquals(List.of(event1, event2), sourceDao.getEventsFromDateTime(LocalDateTime.of(2020, 2, 2, 0, 0, 0)));
        assertEquals(List.of(event2), sourceDao.getEventsFromDateTime(LocalDateTime.of(2020, 3, 2, 0, 0, 0)));
        assertEquals(List.of(event2), sourceDao.getEventsFromDateTime(LocalDateTime.of(2020, 3, 2, 16, 59, 59)));
        assertEquals(Collections.emptyList(), sourceDao.getEventsFromDateTime(LocalDateTime.of(2020, 3, 2, 17, 0, 0)));
        assertEquals(Collections.emptyList(), sourceDao.getEventsFromDateTime(LocalDateTime.of(2020, 3, 2, 17, 0, 1)));
        assertEquals(Collections.emptyList(), sourceDao.getEventsFromDateTime(LocalDateTime.of(2020, 4, 2, 0, 0, 0)));
    }

    @Test
    void getEventByTalk() {
        assertEquals(event0, sourceDao.getEventByTalk(talk0));
        assertEquals(event1, sourceDao.getEventByTalk(talk1));
        assertEquals(event2, sourceDao.getEventByTalk(talk2));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getEventByTalk(talk3));
    }

    @Test
    void getCompanies() {
        assertEquals(List.of(company0, company1, company2), sourceDao.getCompanies());
    }

    @Test
    void getCompanyById() {
        assertEquals(company0, sourceDao.getCompanyById(0));
        assertEquals(company1, sourceDao.getCompanyById(1));
        assertEquals(company2, sourceDao.getCompanyById(2));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getCompanyById(3));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getCompanyById(4));
    }

    @Test
    void getCompaniesByIds() {
        assertEquals(Collections.emptyList(), sourceDao.getCompaniesByIds(Collections.emptyList()));
        assertEquals(List.of(company0), sourceDao.getCompaniesByIds(List.of(0L)));
        assertEquals(List.of(company1), sourceDao.getCompaniesByIds(List.of(1L)));
        assertEquals(List.of(company2), sourceDao.getCompaniesByIds(List.of(2L)));

        assertThat(sourceDao.getCompaniesByIds(List.of(0L, 1L)), containsInAnyOrder(List.of(company0, company1).toArray()));
        assertThat(sourceDao.getCompaniesByIds(List.of(0L, 1L, 2L)), containsInAnyOrder(List.of(company0, company1, company2).toArray()));
    }

    @Test
    void getSpeakers() {
        assertEquals(List.of(speaker0, speaker1, speaker2, speaker3), sourceDao.getSpeakers());
    }

    @Test
    void getSpeakerById() {
        assertEquals(speaker0, sourceDao.getSpeakerById(0));
        assertEquals(speaker1, sourceDao.getSpeakerById(1));
        assertEquals(speaker2, sourceDao.getSpeakerById(2));
        assertEquals(speaker3, sourceDao.getSpeakerById(3));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getSpeakerById(4));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getSpeakerById(5));
    }

    @Test
    void getSpeakerByIds() {
        assertEquals(Collections.emptyList(), sourceDao.getSpeakerByIds(Collections.emptyList()));
        assertEquals(List.of(speaker0), sourceDao.getSpeakerByIds(List.of(0L)));
        assertEquals(List.of(speaker1), sourceDao.getSpeakerByIds(List.of(1L)));
        assertEquals(List.of(speaker2), sourceDao.getSpeakerByIds(List.of(2L)));

        assertThat(sourceDao.getSpeakerByIds(List.of(0L, 1L)), containsInAnyOrder(List.of(speaker0, speaker1).toArray()));
        assertThat(sourceDao.getSpeakerByIds(List.of(0L, 1L, 2L)), containsInAnyOrder(List.of(speaker0, speaker1, speaker2).toArray()));
    }

    @Test
    void getTalks() {
        assertEquals(List.of(talk0, talk1, talk2), sourceDao.getTalks());
    }

    @Test
    void getTalkById() {
        assertEquals(talk0, sourceDao.getTalkById(0));
        assertEquals(talk1, sourceDao.getTalkById(1));
        assertEquals(talk2, sourceDao.getTalkById(2));
        assertThrows(NoSuchElementException.class, () -> sourceDao.getTalkById(3));
    }

    @Test
    void getTalksBySpeaker() {
        assertEquals(List.of(talk0), sourceDao.getTalksBySpeaker(speaker0));
        assertEquals(List.of(talk1), sourceDao.getTalksBySpeaker(speaker1));
        assertEquals(List.of(talk1, talk2), sourceDao.getTalksBySpeaker(speaker2));
        assertEquals(List.of(talk2), sourceDao.getTalksBySpeaker(speaker3));
        assertEquals(Collections.emptyList(), sourceDao.getTalksBySpeaker(speaker4));
        assertEquals(Collections.emptyList(), sourceDao.getTalksBySpeaker(speaker5));
    }

    @Test
    void questionSetsImagesExistence() throws IOException, SpeakerDuplicatedException {
        SourceDao dao = new SourceDaoImpl();
        QuestionDao questionDao = new QuestionDaoImpl(dao, dao);
        List<QuestionSet> questionSets = questionDao.getQuestionSets();

        // All question sets
        for (QuestionSet questionSet : questionSets) {
            List<SpeakerQuestion> speakerQuestions = questionSet.getSpeakerQuestions();
            assertTrue(speakerQuestions.size() > 0);
            // All questions
            for (SpeakerQuestion speakerQuestion : speakerQuestions) {
                assertFileExistence("speakers/" + speakerQuestion.getSpeaker().getPhotoFileName());
            }
            assertFileExistence("events/" + questionSet.getEvent().getEventType().getLogoFileName());
        }
    }

    private void assertFileExistence(String fileName) {
        Path path = Paths.get(String.format("../guess-game-web/src/assets/images/%s", fileName));
        assertTrue(Files.exists(path) && Files.isRegularFile(path),
                String.format("Image file %s does not exist", path.toString()));
    }
}
