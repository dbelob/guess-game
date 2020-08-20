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
 * Account, speakers DTO.
 */
public class AccountSpeakersDto extends EntitySpeakersDto {
    private final String twitter;
    private final String gitHub;

    public AccountSpeakersDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                              Quadruple<Long> ids, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                              Quadruple<String> speakerPhotoFileNames, Quadruple<String> speakerNames,
                              String twitter, String gitHub) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, ids, correctAnswerIds, yourAnswerIds,
                speakerPhotoFileNames, speakerNames);

        this.twitter = twitter;
        this.gitHub = gitHub;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getGitHub() {
        return gitHub;
    }

    public static AccountSpeakersDto convertToDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                                                  QuestionAnswers questionAnswers, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
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
        Speaker questionSpeaker = ((SpeakerQuestion) questionAnswers.getQuestion()).getSpeaker();

        return new AccountSpeakersDto(questionSetName, currentIndex, totalNumber, logoFileName,
                speakers.map(Speaker::getId),
                correctAnswerIds, yourAnswerIds,
                speakers.map(Speaker::getPhotoFileName),
                names,
                questionSpeaker.getTwitter(),
                questionSpeaker.getGitHub());
    }
}
