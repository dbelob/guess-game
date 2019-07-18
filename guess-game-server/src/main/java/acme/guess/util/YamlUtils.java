package acme.guess.util;

import acme.guess.domain.QuestionSet;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        ClassLoader classLoader = YamlUtils.class.getClassLoader();
        File questionDirectory = new File(Objects.requireNonNull(classLoader.getResource(directoryName)).getFile());
        Path questionPath = questionDirectory.toPath();

        List<Path> paths = Files.list(questionPath)
                .filter(p -> (Files.isRegularFile(p) && (p.toString().endsWith(".yml"))))
                .collect(Collectors.toList());

        Yaml yaml = new Yaml(new Constructor(QuestionSet.class));
        List<QuestionSet> questionSets = new ArrayList<>();

        for (Path path : paths) {
            try (InputStream inputStream = Files.newInputStream(path)) {
                questionSets.add(yaml.load(inputStream));
            }
        }

        for (int i = 0; i < questionSets.size(); i++) {
            QuestionSet questionSet = questionSets.get(i);
            questionSet.setId(i);

            for (int j = 0; j < questionSet.getQuestions().size(); j++) {
                questionSet.getQuestions().get(j).setId(j);
            }
        }

        return questionSets;
    }
}
