package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.SpeakerByCompanyQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.Set;

/**
 * Company, speakers DTO.
 */
public class CompanySpeakersDto extends EntitySpeakersDto {
    private final String companyName;

    public CompanySpeakersDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids,
                              Quadruple<String> speakerPhotoFileNames, Quadruple<String> speakerNames,
                              String companyName) {
        super(sourceDto, ids, speakerPhotoFileNames, speakerNames);

        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public static CompanySpeakersDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
                                                  Language language) {
        Quadruple<Speaker> speakers =
                questionAnswers.availableAnswers().map(
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

        return new CompanySpeakersDto(
                sourceDto,
                speakers.map(Speaker::getId),
                speakers.map(Speaker::getPhotoFileName),
                names,
                LocalizationUtils.getString(((SpeakerByCompanyQuestion) questionAnswers.question()).getCompany().getName(), language)
        );
    }
}
