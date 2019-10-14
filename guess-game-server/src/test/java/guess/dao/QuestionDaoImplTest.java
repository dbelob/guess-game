package guess.dao;

import guess.domain.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class QuestionDaoImplTest {
    @Test
    public void questionSetsImagesExistance() throws IOException {
        QuestionDao questionDao = new QuestionDaoImpl();
        List<QuestionSet> questionSets = questionDao.getQuestionSets();

        // All question sets
        for (QuestionSet questionSet : questionSets) {
            List<SpeakerQuestion> speakerQuestions = questionSet.getSpeakerQuestions();

            // All questions
            for (SpeakerQuestion speakerQuestion : speakerQuestions) {
                Path path = Paths.get(String.format("../guess-game-web/src/assets/images/speakers/%s", speakerQuestion.getFileName()));

                Assert.assertTrue(String.format("Image file %s does not exist", path.toString()), Files.exists(path) && Files.isRegularFile(path));
            }
        }
    }
}
