package guess.dao;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("QuestionDao class tests")
class QuestionDaoImplTest {
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
