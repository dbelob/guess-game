package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.Set;

/**
 * Name, photos DTO.
 */
public class NamePhotosDto extends QuestionAnswersDto {
    private final String name;
    private final Quadruple<String> fileNames;

    public NamePhotosDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                         Quadruple<Long> ids, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                         String name, Quadruple<String> fileNames) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, ids, correctAnswerIds, yourAnswerIds);

        this.name = name;
        this.fileNames = fileNames;
    }

    public String getName() {
        return name;
    }

    public String getFileName0() {
        return fileNames.getFirst();
    }

    public String getFileName1() {
        return fileNames.getSecond();
    }

    public String getFileName2() {
        return fileNames.getThird();
    }

    public String getFileName3() {
        return fileNames.getFourth();
    }

    public static NamePhotosDto convertToDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                                             QuestionAnswers questionAnswers, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                                             Language language) {
        Speaker questionSpeaker = ((SpeakerQuestion) questionAnswers.getQuestion()).getSpeaker();
        Quadruple<Speaker> answerSpeakers =
                questionAnswers.getAvailableAnswers().map(
                        a -> ((SpeakerAnswer) a).getSpeaker()
                );
        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                answerSpeakers.asList(),
                language,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);
        String questionName = LocalizationUtils.getSpeakerName(questionSpeaker, language, speakerDuplicates);

        return new NamePhotosDto(questionSetName, currentIndex, totalNumber, logoFileName,
                answerSpeakers.map(Speaker::getId),
                correctAnswerIds, yourAnswerIds,
                questionName,
                answerSpeakers.map(Speaker::getFileName));
    }
}
