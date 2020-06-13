package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.Speaker;
import guess.dto.result.SpeakerPairDto;
import guess.util.LocalizationUtils;

import java.util.List;

/**
 * Speaker, accounts DTO.
 */
public class SpeakerAccountsDto extends QuestionAnswersDto {
    private final SpeakerPairDto speaker;
    private final Quadruple<String> twitters;
    private final Quadruple<String> gitHubs;

    public SpeakerAccountsDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                              Quadruple<Long> ids, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                              SpeakerPairDto speaker,
                              Quadruple<String> twitters, Quadruple<String> gitHubs) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, ids, correctAnswerIds, yourAnswerIds);

        this.speaker = speaker;
        this.twitters = twitters;
        this.gitHubs = gitHubs;
    }

    public SpeakerPairDto getSpeaker() {
        return speaker;
    }

    public String getTwitter0() {
        return twitters.getFirst();
    }

    public String getGitHub0() {
        return gitHubs.getFirst();
    }

    public String getTwitter1() {
        return twitters.getSecond();
    }

    public String getGitHub1() {
        return gitHubs.getSecond();
    }

    public String getTwitter2() {
        return twitters.getThird();
    }

    public String getGitHub2() {
        return gitHubs.getThird();
    }

    public String getTwitter3() {
        return twitters.getFourth();
    }

    public String getGitHub3() {
        return gitHubs.getFourth();
    }

    public static SpeakerAccountsDto convertToDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                                                  QuestionAnswers questionAnswers, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                                                  Language language) {
        Quadruple<Speaker> speakers =
                questionAnswers.getAvailableAnswers().map(
                        a -> ((SpeakerAnswer) a).getSpeaker()
                );
        Speaker questionSpeaker = ((SpeakerQuestion) questionAnswers.getQuestion()).getSpeaker();

        return new SpeakerAccountsDto(questionSetName, currentIndex, totalNumber, logoFileName,
                speakers.map(Speaker::getId),
                correctAnswerIds, yourAnswerIds,
                new SpeakerPairDto(
                        LocalizationUtils.getString(questionSpeaker.getName(), language),
                        questionSpeaker.getFileName()),
                speakers.map(Speaker::getTwitter),
                speakers.map(Speaker::getGitHub));
    }
}
