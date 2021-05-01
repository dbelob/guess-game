package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.Speaker;
import guess.dto.result.SpeakerPairDto;
import guess.util.LocalizationUtils;

/**
 * Speaker, accounts DTO.
 */
public class SpeakerAccountsDto extends QuestionAnswersDto {
    private final SpeakerPairDto speaker;
    private final Quadruple<String> twitters;
    private final Quadruple<String> gitHubs;

    public SpeakerAccountsDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids, SpeakerPairDto speaker,
                              Quadruple<String> twitters, Quadruple<String> gitHubs) {
        super(sourceDto, ids);

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

    public static SpeakerAccountsDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
                                                  Language language) {
        Quadruple<Speaker> speakers =
                questionAnswers.getAvailableAnswers().map(
                        a -> ((SpeakerAnswer) a).getSpeaker()
                );
        var questionSpeaker = ((SpeakerQuestion) questionAnswers.getQuestion()).getSpeaker();

        return new SpeakerAccountsDto(
                sourceDto,
                speakers.map(Speaker::getId),
                new SpeakerPairDto(
                        LocalizationUtils.getString(questionSpeaker.getName(), language),
                        questionSpeaker.getPhotoFileName()),
                speakers.map(Speaker::getTwitter),
                speakers.map(Speaker::getGitHub));
    }
}
