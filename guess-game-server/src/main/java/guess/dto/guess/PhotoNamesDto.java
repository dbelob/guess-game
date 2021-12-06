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
 * Photo, names DTO.
 */
public class PhotoNamesDto extends QuestionAnswersDto {
    private final String photoFileName;
    private final Quadruple<String> names;

    public PhotoNamesDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids, String photoFileName, Quadruple<String> names) {
        super(sourceDto, ids);

        this.photoFileName = photoFileName;
        this.names = names;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public String getName0() {
        return names.first();
    }

    public String getName1() {
        return names.second();
    }

    public String getName2() {
        return names.third();
    }

    public String getName3() {
        return names.fourth();
    }

    public static PhotoNamesDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
                                             Language language) {
        Quadruple<Speaker> speakers =
                questionAnswers.getAvailableAnswers().map(
                        a -> ((SpeakerAnswer) a).getSpeaker()
                );
        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers.asList(),
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);
        Quadruple<String> names =
                speakers.map(
                        s -> LocalizationUtils.getSpeakerName(s, language, speakerDuplicates)
                );

        return new PhotoNamesDto(
                sourceDto,
                speakers.map(Speaker::getId),
                ((SpeakerQuestion) questionAnswers.getQuestion()).getSpeaker().getPhotoFileName(),
                names);
    }
}
