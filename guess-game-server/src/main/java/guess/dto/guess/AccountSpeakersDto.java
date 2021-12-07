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
 * Account, speakers DTO.
 */
public class AccountSpeakersDto extends EntitySpeakersDto {
    private final String twitter;
    private final String gitHub;

    public AccountSpeakersDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids,
                              Quadruple<String> speakerPhotoFileNames, Quadruple<String> speakerNames,
                              String twitter, String gitHub) {
        super(sourceDto, ids, speakerPhotoFileNames, speakerNames);

        this.twitter = twitter;
        this.gitHub = gitHub;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getGitHub() {
        return gitHub;
    }

    public static AccountSpeakersDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
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
        var questionSpeaker = ((SpeakerQuestion) questionAnswers.question()).getSpeaker();

        return new AccountSpeakersDto(
                sourceDto,
                speakers.map(Speaker::getId),
                speakers.map(Speaker::getPhotoFileName),
                names,
                questionSpeaker.getTwitter(),
                questionSpeaker.getGitHub());
    }
}
