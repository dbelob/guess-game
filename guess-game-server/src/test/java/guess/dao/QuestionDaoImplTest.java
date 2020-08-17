package guess.dao;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;
import guess.domain.source.Event;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
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
        @MockBean
        private EventTypeDao eventTypeDao;

        @MockBean
        private EventDao eventDao;

        @Bean
        public QuestionDao questionDao() {
            return new QuestionDaoImpl(eventTypeDao, eventDao);
        }
    }

    @Autowired
    private EventTypeDao eventTypeDao;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private QuestionDao questionDao;

    private Speaker speaker0;
    private Speaker speaker1;
    private Speaker speaker2;
    private Talk talk0;
    private Talk talk1;
    private Talk talk2;
    private Event event0;
    private Event event1;
    private Event event2;

    @BeforeEach
    public void setUp() {
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
        talk2.setSpeakers(List.of(speaker2));

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

        Mockito.when(eventDao.getEvents()).thenReturn(List.of(event0, event1, event2));
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
                                        new SpeakerQuestion(speaker2)
                                ),
                                List.of(
                                        new TalkQuestion(List.of(speaker2), talk2)
                                ),
                                List.of(new SpeakerQuestion(speaker2))
                        )
                ),
                questionDao.readQuestionSets());
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
