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
 * Photo, names DTO.
 */
public class PhotoNamesDto extends QuestionAnswersDto {
    private final String fileName;
    private final Quadruple<String> names;

    public PhotoNamesDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                         Quadruple<Long> ids, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                         String fileName, Quadruple<String> names) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, ids, correctAnswerIds, yourAnswerIds);

        this.fileName = fileName;
        this.names = names;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName0() {
        return names.getFirst();
    }

    public String getName1() {
        return names.getSecond();
    }

    public String getName2() {
        return names.getThird();
    }

    public String getName3() {
        return names.getFourth();
    }

    public static PhotoNamesDto convertToDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                                             QuestionAnswers questionAnswers, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                                             Language language) {
        Quadruple<Speaker> speakers =
                questionAnswers.getAvailableAnswers().map(
                        a -> ((SpeakerAnswer) a).getSpeaker()
                );
        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers.asList(),
                language,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);
        Quadruple<String> names =
                speakers.map(
                        s -> LocalizationUtils.getSpeakerName(s, language, speakerDuplicates)
                );

        return new PhotoNamesDto(questionSetName, currentIndex, totalNumber, logoFileName,
                speakers.map(Speaker::getId),
                correctAnswerIds, yourAnswerIds,
                ((SpeakerQuestion) questionAnswers.getQuestion()).getSpeaker().getFileName(),
                names);
    }
}
