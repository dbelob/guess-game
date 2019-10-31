package guess.util;

import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class with the methods needed to use the source data in the 'questions' resource directory.
 * <p>
 * Must be deleted:
 * <ol>
 * <li>'questions' resource directory</li>
 * <li>guess.util.Unsafe class</li>
 * <li>guess.util.UnsafeQuestionSet class</li>
 * <li>guess.util.UnsafeSpeakerQuestion class</li>
 * </ol>
 */
//TODO: delete
class Unsafe {
    static void replaceSpeakerQuestions(List<QuestionSet> questionSets, String questionsDirectoryName,
                                        List<Speaker> speakers) throws IOException {
        Map<String, Speaker> speakerMap = YamlUtils.listToMap(speakers, Speaker::getFileName);
        List<QuestionSet> speakerQuestionSets = readSpeakerQuestionSets(questionsDirectoryName, speakerMap);
        Map<Long, QuestionSet> speakerQuestionSetMap = YamlUtils.listToMap(speakerQuestionSets, QuestionSet::getId);

        for (QuestionSet questionSet : questionSets) {
            // Find speaker question set by id
            QuestionSet speakerQuestionSet = speakerQuestionSetMap.get(questionSet.getId());
            Objects.requireNonNull(speakerQuestionSet,
                    () -> String.format("Speaker question set id %d not found", questionSet.getId()));

            questionSet.setSpeakerQuestions(speakerQuestionSet.getSpeakerQuestions());
        }
    }

    private static List<QuestionSet> readSpeakerQuestionSets(String questionsDirectoryName,
                                                             Map<String, Speaker> speakerMap) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(String.format("classpath:%s/*.yml", questionsDirectoryName));
        Yaml yaml = new Yaml(new Constructor(UnsafeQuestionSet.class));
        List<UnsafeQuestionSet> unsafeQuestionSets = new ArrayList<>();
        List<QuestionSet> questionSets = new ArrayList<>();

        // Read question sets from YAML files
        for (Resource resource : resources) {
            unsafeQuestionSets.add(yaml.load(resource.getInputStream()));
        }

        long questionId = 0;
        for (UnsafeQuestionSet unsafeQuestionSet : unsafeQuestionSets) {
            List<SpeakerQuestion> speakerQuestions = unsafeQuestionSet.getSpeakerQuestions().stream()
                    .map(q -> {
                                //TODO: uncomment
//                                Speaker speaker = speakerMap.get(q.getFileName());
//                                Objects.requireNonNull(speaker,
//                                        () -> String.format("Speaker filename %s not found", q.getFileName()));
//
//                                return new SpeakerQuestion(speaker);

                                //TODO: delete
                                return new SpeakerQuestion(new Speaker(
                                        q.getId(),
                                        q.getFileName(),
                                        Collections.singletonList(new LocaleItem(LocalizationUtils.ENGLISH_LANGUAGE, q.getName()))));
                            }
                    )
                    .collect(Collectors.toList());

            // Remove duplicates by filename
            speakerQuestions = QuestionUtils.removeDuplicatesByFileName(speakerQuestions);

            // Set unique id
            for (SpeakerQuestion speakerQuestion : speakerQuestions) {
                speakerQuestion.setId(questionId++);
            }

            questionSets.add(new QuestionSet(
                    unsafeQuestionSet.getId(),
                    unsafeQuestionSet.getName(),
                    unsafeQuestionSet.getLogoFileName(),
                    QuestionUtils.removeDuplicatesByFileName(speakerQuestions),
                    Collections.emptyList()));
        }

        return questionSets;
    }
}
