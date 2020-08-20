package guess.dao;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Conference;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;
import guess.domain.source.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("QuestionDaoImpl class tests")
@ExtendWith(SpringExtension.class)
class QuestionDaoImplTest {
    @TestConfiguration
    static class QuestionDaoImplTestContextConfiguration {
        @Bean
        public QuestionDao questionDao() {
            SourceInformation sourceInformation = new SourceInformation(
                    Collections.emptyList(),
                    List.of(eventType0, eventType1, eventType2),
                    List.of(event0, event1, event2, event3),
                    List.of(speaker0, speaker1, speaker2, speaker3),
                    List.of(talk0, talk1, talk2));
            SourceDao sourceDao = new SourceDaoImpl(sourceInformation);

            return new QuestionDaoImpl(sourceDao, sourceDao);
        }
    }

    @Autowired
    private QuestionDao questionDao;

    private static final Speaker speaker0;
    private static final Speaker speaker1;
    private static final Speaker speaker2;
    private static final Speaker speaker3;
    private static final Talk talk0;
    private static final Talk talk1;
    private static final Talk talk2;
    private static final Event event0;
    private static final Event event1;
    private static final Event event2;
    private static final Event event3;
    private static final EventType eventType0;
    private static final EventType eventType1;
    private static final EventType eventType2;

    static {
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

        eventType0 = new EventType();
        eventType0.setId(0);
        eventType0.setConference(Conference.JPOINT);
        eventType0.setEvents(List.of(event0, event3));
        event0.setEventTypeId(0);
        event0.setEventType(eventType0);
        event3.setEventTypeId(0);
        event3.setEventType(eventType0);

        eventType1 = new EventType();
        eventType1.setId(1);
        eventType1.setEvents(List.of(event1));
        event1.setEventTypeId(1);
        event1.setEventType(eventType1);

        eventType2 = new EventType();
        eventType2.setId(2);
        eventType2.setEvents(List.of(event2));
        event2.setEventTypeId(2);
        event2.setEventType(eventType2);
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
                                List.of(new SpeakerQuestion(speaker2))
                        ),
                        new QuestionSet(
                                event3,
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
