package guess.util;

import guess.domain.question.QuestionSet;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class with the methods needed to use the source data in the 'questions' resource directory.
 * <p>
 * The 'questions' resource directory and class must be deleted.
 */
//TODO: delete
class Unsafe {
    static void replaceSpeakerQuestions(List<QuestionSet> questionSets, String questionsDirectoryName) throws IOException {
        List<QuestionSet> speakerQuestionSets = readSpeakerQuestionSets(questionsDirectoryName);

        for (QuestionSet questionSet : questionSets) {
            for (QuestionSet speakerQuestionSet : speakerQuestionSets) {
                if (questionSet.getId() == speakerQuestionSet.getId()) {
                    questionSet.setSpeakerQuestions(speakerQuestionSet.getSpeakerQuestions());
                }
            }
        }
    }

    private static List<QuestionSet> readSpeakerQuestionSets(String questionsDirectoryName) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(String.format("classpath:%s/*.yml", questionsDirectoryName));
        Yaml yaml = new Yaml(new Constructor(QuestionSet.class));
        List<QuestionSet> questionSets = new ArrayList<>();

        // Read question sets from YAML files
        for (Resource resource : resources) {
            questionSets.add(yaml.load(resource.getInputStream()));
        }

        long questionId = 0;
        for (QuestionSet questionSet : questionSets) {
            // Remove duplicates by filename
            questionSet.setSpeakerQuestions(QuestionUtils.removeDuplicatesByFileName(questionSet.getSpeakerQuestions()));

            // Set unique id
            for (int j = 0; j < questionSet.getSpeakerQuestions().size(); j++) {
                questionSet.getSpeakerQuestions().get(j).setId(questionId++);
            }
        }

        return questionSets;
    }
}