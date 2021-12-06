package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.CompanyAnswer;
import guess.domain.question.CompanyBySpeakerQuestion;
import guess.domain.question.QuestionAnswers;
import guess.domain.source.Company;
import guess.dto.result.SpeakerPairDto;
import guess.util.LocalizationUtils;

/**
 * Speaker, companies DTO.
 */
public class SpeakerCompaniesDto extends QuestionAnswersDto {
    private final SpeakerPairDto speaker;
    private final Quadruple<String> companyNames;

    protected SpeakerCompaniesDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids, SpeakerPairDto speaker,
                                  Quadruple<String> companyNames) {
        super(sourceDto, ids);

        this.speaker = speaker;
        this.companyNames = companyNames;
    }

    public SpeakerPairDto getSpeaker() {
        return speaker;
    }

    public String getCompanyName0() {
        return companyNames.first();
    }

    public String getCompanyName1() {
        return companyNames.second();
    }

    public String getCompanyName2() {
        return companyNames.third();
    }

    public String getCompanyName3() {
        return companyNames.fourth();
    }

    public static SpeakerCompaniesDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
                                                   Language language) {
        Quadruple<Company> companies =
                questionAnswers.getAvailableAnswers().map(
                        a -> ((CompanyAnswer) a).getCompany()
                );
        var questionSpeaker = ((CompanyBySpeakerQuestion) questionAnswers.getQuestion()).getSpeaker();

        return new SpeakerCompaniesDto(
                sourceDto,
                companies.map(Company::getId),
                new SpeakerPairDto(
                        LocalizationUtils.getString(questionSpeaker.getName(), language),
                        questionSpeaker.getPhotoFileName()),
                companies.map(c -> LocalizationUtils.getString(c.getName(), language))
        );
    }
}
