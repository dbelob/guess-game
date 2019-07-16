package acme.guess.util;

import acme.guess.domain.QuestionSet;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class YamlUtils {
    public static List<QuestionSet> readQuestionSets() {
        Yaml yaml = new Yaml(new Constructor(QuestionSet.class));
        InputStream inputStream = YamlUtils.class
                .getClassLoader()
                .getResourceAsStream("questions/sample1.yml");
        QuestionSet questionSet = yaml.load(inputStream);

        return Collections.singletonList(questionSet);
    }
}