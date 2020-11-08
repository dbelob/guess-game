package guess.dao;

import guess.domain.Conference;
import guess.domain.GuessMode;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("QuestionDaoImpl class tests")
class QuestionDaoImplTest {
    private static Speaker speaker0;
    private static Speaker speaker1;
    private static Speaker speaker2;
    private static Speaker speaker3;

    private static Talk talk0;
    private static Talk talk1;
    private static Talk talk2;

    private static Event event0;
    private static Event event1;
    private static Event event2;
    private static Event event3;

    private static QuestionDao questionDao;

    @BeforeAll
    static void init() {
        speaker0 = new Speaker();
        speaker0.setId(0);

        speaker1 = new Speaker();
        speaker1.setId(1);
        speaker1.setTwitter("twitter1");
        speaker1.setGitHub("");

        speaker2 = new Speaker();
        speaker2.setId(2);
        speaker2.setTwitter("");
        speaker2.setGitHub("gitHub2");

        speaker3 = new Speaker();
        speaker3.setId(3);
        speaker3.setTwitter("");
        speaker3.setGitHub("");

        talk0 = new Talk();
        talk0.setId(0);
        talk0.setSpeakerIds(List.of(0L));
        talk0.setSpeakers(List.of(speaker0));

        talk1 = new Talk();
        talk1.setId(1);
        talk1.setSpeakerIds(List.of(1L));
        talk1.setSpeakers(List.of(speaker1));

        talk2 = new Talk();
        talk2.setId(2);
        talk2.setSpeakerIds(List.of(2L));
        talk2.setSpeakers(List.of(speaker2, speaker3));

        event0 = new Event();
        event0.setId(0);
        event0.setTalkIds(List.of(0L));
        event0.setTalks(List.of(talk0));

        event1 = new Event();
        event1.setId(1);
        event1.setTalkIds(List.of(1L));
        event1.setTalks(List.of(talk1));

        event2 = new Event();
        event2.setId(2);
        event2.setTalkIds(List.of(2L));
        event2.setTalks(List.of(talk2));

        event3 = new Event();
        event3.setId(3);

        EventType eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);
        eventType0.setEvents(List.of(event0, event3));
        event0.setEventTypeId(0);
        event0.setEventType(eventType0);
        event3.setEventTypeId(0);
        event3.setEventType(eventType0);

        EventType eventType1 = new EventType();
        eventType1.setId(1);
        eventType1.setEvents(List.of(event1));
        event1.setEventTypeId(1);
        event1.setEventType(eventType1);

        EventType eventType2 = new EventType();
        eventType2.setId(2);
        eventType2.setEvents(List.of(event2));
        event2.setEventTypeId(2);
        event2.setEventType(eventType2);

        EventTypeDao eventTypeDao = Mockito.mock(EventTypeDao.class);
        Mockito.when(eventTypeDao.getEventTypeById(0)).thenReturn(eventType0);
        Mockito.when(eventTypeDao.getEventTypeById(1)).thenReturn(eventType1);

        EventDao eventDao = Mockito.mock(EventDao.class);
        Mockito.when(eventDao.getEvents()).thenReturn(List.of(event0, event1, event2, event3));

        questionDao = new QuestionDaoImpl(eventTypeDao, eventDao);
    }

    @Test
    void readQuestionSets() {
        assertEquals(
                List.of(new QuestionSet(
                                event0,
                                List.of(
                                        new SpeakerQuestion(speaker0)
                                ),
                                List.of(
                                        new TalkQuestion(List.of(speaker0), talk0)
                                ),
                                Collections.emptyList(),
                                Collections.emptyList(),
                                Collections.emptyList()
                        ),
                        new QuestionSet(
                                event1,
                                List.of(
                                        new SpeakerQuestion(speaker1)
                                ),
                                List.of(
                                        new TalkQuestion(List.of(speaker1), talk1)
                                ),
                                Collections.emptyList(),
                                Collections.emptyList(),
                                List.of(new SpeakerQuestion(speaker1))
                        ),
                        new QuestionSet(
                                event2,
                                List.of(
                                        new SpeakerQuestion(speaker2),
                                        new SpeakerQuestion(speaker3)
                                ),
                                List.of(
                                        new TalkQuestion(List.of(speaker2, speaker3), talk2)
                                ),
                                Collections.emptyList(),
                                Collections.emptyList(),
                                List.of(new SpeakerQuestion(speaker2))
                        ),
                        new QuestionSet(
                                event3,
                                Collections.emptyList(),
                                Collections.emptyList(),
                                Collections.emptyList(),
                                Collections.emptyList(),
                                Collections.emptyList()
                        )
                ),
                questionDao.readQuestionSets());
    }

    @Test
    void getSubQuestionSets() {
        assertEquals(
                Collections.emptyList(),
                questionDao.getSubQuestionSets(
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        );
        assertEquals(
                Collections.emptyList(),
                questionDao.getSubQuestionSets(
                        Collections.singletonList(null),
                        Collections.emptyList()
                )
        );
        assertEquals(
                List.of(new QuestionSet(
                        event0,
                        List.of(
                                new SpeakerQuestion(speaker0)
                        ),
                        List.of(
                                new TalkQuestion(List.of(speaker0), talk0)
                        ),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                )),
                questionDao.getSubQuestionSets(
                        Collections.singletonList(0L),
                        Collections.singletonList(0L)
                )
        );
        assertEquals(
                List.of(new QuestionSet(
                        event3,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                )),
                questionDao.getSubQuestionSets(
                        Collections.singletonList(0L),
                        Collections.singletonList(3L)
                )
        );
        assertEquals(
                List.of(new QuestionSet(
                        event1,
                        List.of(
                                new SpeakerQuestion(speaker1)
                        ),
                        List.of(
                                new TalkQuestion(List.of(speaker1), talk1)
                        ),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        List.of(new SpeakerQuestion(speaker1))
                )),
                questionDao.getSubQuestionSets(
                        Collections.singletonList(1L),
                        Collections.singletonList(1L)
                )
        );
        assertEquals(
                List.of(new QuestionSet(
                                event1,
                                List.of(
                                        new SpeakerQuestion(speaker1)
                                ),
                                List.of(
                                        new TalkQuestion(List.of(speaker1), talk1)
                                ),
                                Collections.emptyList(),
                                Collections.emptyList(),
                                List.of(new SpeakerQuestion(speaker1))
                        ),
                        new QuestionSet(
                                event2,
                                List.of(
                                        new SpeakerQuestion(speaker2),
                                        new SpeakerQuestion(speaker3)
                                ),
                                List.of(
                                        new TalkQuestion(List.of(speaker2, speaker3), talk2)
                                ),
                                Collections.emptyList(),
                                Collections.emptyList(),
                                List.of(new SpeakerQuestion(speaker2))
                        )
                ),
                questionDao.getSubQuestionSets(
                        List.of(1L, 2L),
                        Collections.emptyList()
                )
        );
    }

    @Test
    void getQuestionByIds() {
        assertEquals(
                Collections.emptyList(),
                questionDao.getQuestionByIds(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        GuessMode.GUESS_NAME_BY_PHOTO_MODE
                )
        );
        assertEquals(
                Collections.emptyList(),
                questionDao.getQuestionByIds(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        GuessMode.GUESS_PHOTO_BY_NAME_MODE
                )
        );
        assertEquals(
                Collections.emptyList(),
                questionDao.getQuestionByIds(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        GuessMode.GUESS_TALK_BY_SPEAKER_MODE
                )
        );
        assertEquals(
                Collections.emptyList(),
                questionDao.getQuestionByIds(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        GuessMode.GUESS_SPEAKER_BY_TALK_MODE
                )
        );
        assertEquals(
                Collections.emptyList(),
                questionDao.getQuestionByIds(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        GuessMode.GUESS_ACCOUNT_BY_SPEAKER_MODE
                )
        );
        assertEquals(
                Collections.emptyList(),
                questionDao.getQuestionByIds(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        GuessMode.GUESS_SPEAKER_BY_ACCOUNT_MODE
                )
        );
        assertEquals(
                List.of(
                        new SpeakerQuestion(speaker1),
                        new SpeakerQuestion(speaker2),
                        new SpeakerQuestion(speaker3)
                ),
                questionDao.getQuestionByIds(
                        List.of(1L, 2L),
                        Collections.emptyList(),
                        GuessMode.GUESS_NAME_BY_PHOTO_MODE
                )
        );
        assertEquals(
                List.of(
                        new SpeakerQuestion(speaker1),
                        new SpeakerQuestion(speaker2),
                        new SpeakerQuestion(speaker3)
                ),
                questionDao.getQuestionByIds(
                        List.of(1L, 2L),
                        Collections.emptyList(),
                        GuessMode.GUESS_PHOTO_BY_NAME_MODE
                )
        );
        assertEquals(
                List.of(
                        new TalkQuestion(List.of(speaker1), talk1),
                        new TalkQuestion(List.of(speaker2, speaker3), talk2)
                ),
                questionDao.getQuestionByIds(
                        List.of(1L, 2L),
                        Collections.emptyList(),
                        GuessMode.GUESS_TALK_BY_SPEAKER_MODE
                )
        );
        assertEquals(
                List.of(
                        new TalkQuestion(List.of(speaker1), talk1),
                        new TalkQuestion(List.of(speaker2, speaker3), talk2)
                ),
                questionDao.getQuestionByIds(
                        List.of(1L, 2L),
                        Collections.emptyList(),
                        GuessMode.GUESS_SPEAKER_BY_TALK_MODE
                )
        );
        assertEquals(
                List.of(
                        new SpeakerQuestion(speaker1),
                        new SpeakerQuestion(speaker2)
                ),
                questionDao.getQuestionByIds(
                        List.of(1L, 2L),
                        Collections.emptyList(),
                        GuessMode.GUESS_ACCOUNT_BY_SPEAKER_MODE
                )
        );
        assertEquals(
                List.of(
                        new SpeakerQuestion(speaker1),
                        new SpeakerQuestion(speaker2)
                ),
                questionDao.getQuestionByIds(
                        List.of(1L, 2L),
                        Collections.emptyList(),
                        GuessMode.GUESS_SPEAKER_BY_ACCOUNT_MODE
                )
        );

        List<Long> emptyEventTypeIds = Collections.emptyList();
        List<Long> emptyEventIds = Collections.emptyList();

        assertThrows(IllegalArgumentException.class, () -> questionDao.getQuestionByIds(
                emptyEventTypeIds,
                emptyEventIds,
                null
        ));
    }
}
