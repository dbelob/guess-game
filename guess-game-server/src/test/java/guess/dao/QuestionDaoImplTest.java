package guess.dao;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class QuestionDaoImplTest {
    @Test
    public void questionSetsImagesExistance() throws IOException, SpeakerDuplicatedException {
        SourceDaoImpl sourceDao = new SourceDaoImpl();
        QuestionDao questionDao = new QuestionDaoImpl(sourceDao, sourceDao);
        List<QuestionSet> questionSets = questionDao.getQuestionSets();

        // All question sets
        for (QuestionSet questionSet : questionSets) {
            List<SpeakerQuestion> speakerQuestions = questionSet.getSpeakerQuestions();
            assertTrue(speakerQuestions.size() > 0);
            // All questions
            for (SpeakerQuestion speakerQuestion : speakerQuestions) {
                assertFileExistence("speakers/" + speakerQuestion.getSpeaker().getFileName());
            }
            assertFileExistence("events/" + questionSet.getLogoFileName());
        }
    }

    private void assertFileExistence(String fileName) {
        Path path = Paths.get(String.format("../guess-game-web/src/assets/images/%s", fileName));
        assertTrue(String.format("Image file %s does not exist", path.toString()),
                Files.exists(path) && Files.isRegularFile(path));
    }
}
