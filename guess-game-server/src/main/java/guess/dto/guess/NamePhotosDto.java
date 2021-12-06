package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.Set;

/**
 * Name, photos DTO.
 */
public class NamePhotosDto extends QuestionAnswersDto {
    private final String name;
    private final Quadruple<String> photoFileNames;

    public NamePhotosDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids, String name, Quadruple<String> photoFileNames) {
        super(sourceDto, ids);

        this.name = name;
        this.photoFileNames = photoFileNames;
    }

    public String getName() {
        return name;
    }

    public String getPhotoFileName0() {
        return photoFileNames.first();
    }

    public String getPhotoFileName1() {
        return photoFileNames.second();
    }

    public String getPhotoFileName2() {
        return photoFileNames.third();
    }

    public String getPhotoFileName3() {
        return photoFileNames.fourth();
    }

    public static NamePhotosDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
                                             Language language) {
        var questionSpeaker = ((SpeakerQuestion) questionAnswers.getQuestion()).getSpeaker();
        Quadruple<Speaker> answerSpeakers =
                questionAnswers.getAvailableAnswers().map(
                        a -> ((SpeakerAnswer) a).getSpeaker()
                );
        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                answerSpeakers.asList(),
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);
        String questionName = LocalizationUtils.getSpeakerName(questionSpeaker, language, speakerDuplicates);

        return new NamePhotosDto(
                sourceDto,
                answerSpeakers.map(Speaker::getId),
                questionName,
                answerSpeakers.map(Speaker::getPhotoFileName));
    }
}
