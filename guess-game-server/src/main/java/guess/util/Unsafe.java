package guess.util;

import guess.domain.Language;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import guess.domain.source.Speakers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(Unsafe.class);

    private static String DESCRIPTIONS_DIRECTORY_NAME = "descriptions";
    private static String QUESTIONS_DIRECTORY_NAME = "questions";

    static void replaceSpeakerQuestions(List<QuestionSet> questionSets, List<Speaker> speakers) throws IOException {
        Map<String, Speaker> speakerMap = YamlUtils.listToMap(speakers, Speaker::getFileName);
        List<QuestionSet> speakerQuestionSets = readSpeakerQuestionSets(speakerMap);
        Map<Long, QuestionSet> speakerQuestionSetMap = YamlUtils.listToMap(speakerQuestionSets, QuestionSet::getId);

        for (QuestionSet questionSet : questionSets) {
            // Find speaker question set by id
            QuestionSet speakerQuestionSet = speakerQuestionSetMap.get(questionSet.getId());

            if (speakerQuestionSet != null) {
                questionSet.setSpeakerQuestions(speakerQuestionSet.getSpeakerQuestions());
            }
        }
    }

    private static List<QuestionSet> readSpeakerQuestionSets(Map<String, Speaker> speakerMap) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(String.format("classpath:%s/*.yml", QUESTIONS_DIRECTORY_NAME));
        Yaml yaml = new Yaml(new Constructor(UnsafeQuestionSet.class));
        List<UnsafeQuestionSet> unsafeQuestionSets = new ArrayList<>();
        List<QuestionSet> questionSets = new ArrayList<>();

        // Read question sets from YAML files
        for (Resource resource : resources) {
            unsafeQuestionSets.add(yaml.load(resource.getInputStream()));
        }

        for (UnsafeQuestionSet unsafeQuestionSet : unsafeQuestionSets) {
            List<SpeakerQuestion> speakerQuestions = unsafeQuestionSet.getSpeakerQuestions().stream()
                    .map(q -> {
                                Speaker speaker = speakerMap.get(q.getFileName());
                                Objects.requireNonNull(speaker,
                                        () -> String.format("Speaker filename %s (name '%s') not found", q.getFileName(), q.getName()));

                                return new SpeakerQuestion(speaker);
                            }
                    )
                    .collect(Collectors.toList());

            questionSets.add(new QuestionSet(
                    unsafeQuestionSet.getId(),
                    Collections.singletonList(new LocaleItem(Language.ENGLISH.getCode(), unsafeQuestionSet.getName())),
                    unsafeQuestionSet.getLogoFileName(),
                    QuestionUtils.removeDuplicatesById(speakerQuestions),
                    Collections.emptyList()));
        }

        return questionSets;
    }

    public static void main(String[] args) throws IOException {
        // Create YAML files for absent speakers

        // Read speakers from speakers.yml file
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource speakersResource = resolver.getResource(String.format("classpath:%s/speakers.yml", DESCRIPTIONS_DIRECTORY_NAME));
        Yaml speakersYaml = new Yaml(new Constructor(Speakers.class));

        Speakers speakers = speakersYaml.load(speakersResource.getInputStream());
        Map<String, Speaker> yamlFilenameSpeakerMap = YamlUtils.listToMap(speakers.getSpeakers(), Speaker::getFileName);

        // Read name, speaker map from Contentful
        Map<String, Speaker> contentfulNameSpeakerMap = ContentfulUtils.getNameSpeakerMap();

        // Read speakers from conferences-*.yml and meetups-*.yml files
        Resource[] resources = resolver.getResources(String.format("classpath:%s/*.yml", QUESTIONS_DIRECTORY_NAME));
        Yaml unsafeQuestionSetYaml = new Yaml(new Constructor(UnsafeQuestionSet.class));
        List<UnsafeQuestionSet> unsafeQuestionSets = new ArrayList<>();

        // Read question sets from YAML files
        for (Resource resource : resources) {
            unsafeQuestionSets.add(unsafeQuestionSetYaml.load(resource.getInputStream()));
        }

        // Delete duplication of filename, speaker mapping
        Map<String, UnsafeSpeakerQuestion> yamlFilenameUnsafeSpeakerQuestionMap =
                unsafeQuestionSets.stream()
                        .flatMap(unsafeQuestionSet -> unsafeQuestionSet.getSpeakerQuestions().stream())
                        .collect(Collectors.toMap(UnsafeSpeakerQuestion::getFileName,
                                speakerQuestion -> speakerQuestion,
                                (a, b) -> b,
                                TreeMap::new));

        List<Speaker> absentSpeakers = new ArrayList<>();

        for (String filename : yamlFilenameUnsafeSpeakerQuestionMap.keySet()) {
            UnsafeSpeakerQuestion speakerQuestion = yamlFilenameUnsafeSpeakerQuestionMap.get(filename);

            // Speaker found in speakers.yml file by image filename
            if (yamlFilenameSpeakerMap.containsKey(speakerQuestion.getFileName())) {
                continue;
            }

            // Speaker found in Contentful
            Speaker contentfulSpeaker = contentfulNameSpeakerMap.get(speakerQuestion.getName());

            if (contentfulSpeaker != null) {
                long id = Long.parseLong(filename.substring(0, filename.indexOf(".")));
                List<LocaleItem> name = new ArrayList<LocaleItem>() {{
                    add(new LocaleItem(Language.ENGLISH.getCode(), speakerQuestion.getName()));
                }};
                List<LocaleItem> company = Collections.emptyList();

                String russianName = LocalizationUtils.getString(contentfulSpeaker.getName(), Language.RUSSIAN);

                if ((russianName != null) && !russianName.isEmpty() && !russianName.equals(speakerQuestion.getName())) {
                    name.add(new LocaleItem(Language.RUSSIAN.getCode(), russianName));
                }

                Speaker speaker = new Speaker(
                        id,
                        filename,
                        name,
                        company);

                absentSpeakers.add(speaker);
            }
        }

        printSpeakers(absentSpeakers);
    }

    private static void printSpeakers(List<Speaker> speakers) {
        StringBuilder sb = new StringBuilder();

        speakers.forEach(s -> {
            sb.append(String.format(
                    "  - id: %d\n" +
                            "    fileName: %s\n" +
                            "    name:\n", s.getId(), s.getFileName()));

            for (LocaleItem localeItem : s.getName()) {
                sb.append(String.format("      - {language: %s, text: \"%s\"}\n", localeItem.getLanguage(), localeItem.getText()));
            }
        });

        System.out.print(sb.toString());
    }
}
