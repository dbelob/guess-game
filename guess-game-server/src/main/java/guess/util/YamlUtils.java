package guess.util;

import guess.domain.Question;
import guess.domain.QuestionSet;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * YAML utility methods.
 */
public class YamlUtils {
    /**
     * Reads question sets from resource files.
     *
     * @param directoryName directory name
     * @return question sets
     * @throws IOException if an I/O error occurs
     */
    public static List<QuestionSet> readQuestionSets(String directoryName) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(String.format("classpath:%s/*.yml", directoryName));
        Yaml yaml = new Yaml(new Constructor(QuestionSet.class));
        List<QuestionSet> questionSets = new ArrayList<>();

        for (Resource resource : resources) {
            questionSets.add(yaml.load(resource.getInputStream()));
        }

        for (int i = 0; i < questionSets.size(); i++) {
            QuestionSet questionSet = questionSets.get(i);
            questionSet.setId(i);

            questionSet.setQuestions(removeDuplicatesByFileName(questionSet.getQuestions()));

            for (int j = 0; j < questionSet.getQuestions().size(); j++) {
                questionSet.getQuestions().get(j).setId(j);
            }
        }

        return questionSets;
    }

    private static List<Question> removeDuplicatesByFileName(List<Question> questions) {
        List<Question> result = new ArrayList<>();
        Set<String> fileNames = new HashSet<>();

        for (Question question : questions) {
            if (!fileNames.contains(question.getFileName())) {
                result.add(question);
                fileNames.add(question.getFileName());
            }
        }

        return result;
    }
}
