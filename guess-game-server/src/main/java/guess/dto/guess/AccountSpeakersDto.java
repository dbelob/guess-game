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
public class AccountSpeakersDto extends QuestionAnswersDto {
    private final String twitter;
    private final String gitHub;
    private final Quadruple<String> speakerFileNames;
    private final Quadruple<String> speakerNames;

    public AccountSpeakersDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                              Quadruple<Long> ids, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                              String twitter, String gitHub,
                              Quadruple<String> speakerFileNames, Quadruple<String> speakerNames) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, ids, correctAnswerIds, yourAnswerIds);

        this.twitter = twitter;
        this.gitHub = gitHub;
        this.speakerFileNames = speakerFileNames;
        this.speakerNames = speakerNames;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getGitHub() {
        return gitHub;
    }

    public String getSpeakerFileName0() {
        return speakerFileNames.getFirst();
    }

    public String getSpeakerName0() {
        return speakerNames.getFirst();
    }

    public String getSpeakerFileName1() {
        return speakerFileNames.getSecond();
    }

    public String getSpeakerName1() {
        return speakerNames.getSecond();
    }

    public String getSpeakerFileName2() {
        return speakerFileNames.getThird();
    }

    public String getSpeakerName2() {
        return speakerNames.getThird();
    }

    public String getSpeakerFileName3() {
        return speakerFileNames.getFourth();
    }

    public String getSpeakerName3() {
        return speakerNames.getFourth();
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
                questionSpeaker.getTwitter(),
                questionSpeaker.getGitHub(),
                speakers.map(Speaker::getFileName),
                names);
    }
}
