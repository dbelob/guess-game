package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.TalkAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.TalkQuestion;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.dto.result.SpeakerPairDto;
import guess.util.LocalizationUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Speakers, talks DTO.
 */
public class SpeakersTalksDto extends QuestionAnswersDto {
    private final List<SpeakerPairDto> speakers;
    private final Quadruple<String> talkNames;

    public SpeakersTalksDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                            Quadruple<Long> ids, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                            List<SpeakerPairDto> speakers,
                            Quadruple<String> talkNames) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, ids, correctAnswerIds, yourAnswerIds);

        this.speakers = speakers;
        this.talkNames = talkNames;
    }

    public List<SpeakerPairDto> getSpeakers() {
        return speakers;
    }

    public String getTalkName0() {
        return talkNames.getFirst();
    }

    public String getTalkName1() {
        return talkNames.getSecond();
    }

    public String getTalkName2() {
        return talkNames.getThird();
    }

    public String getTalkName3() {
        return talkNames.getFourth();
    }

    public static SpeakersTalksDto convertToDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                                                QuestionAnswers questionAnswers, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                                                Language language) {
        Quadruple<Talk> talks =
                questionAnswers.getAvailableAnswers().map(
                        a -> ((TalkAnswer) a).getTalk()
                );

        Set<Speaker> talkSpeakers = new HashSet<>();
        talkSpeakers.addAll(talks.getFirst().getSpeakers());
        talkSpeakers.addAll(talks.getSecond().getSpeakers());
        talkSpeakers.addAll(talks.getThird().getSpeakers());
        talkSpeakers.addAll(talks.getFourth().getSpeakers());

        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                new ArrayList<>(talkSpeakers),
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);
        List<SpeakerPairDto> questionSpeakers = ((TalkQuestion) questionAnswers.getQuestion()).getSpeakers().stream()
                .map(s -> new SpeakerPairDto(
                        LocalizationUtils.getSpeakerName(s, language, speakerDuplicates),
                        s.getPhotoFileName()))
                .collect(Collectors.toList());

        return new SpeakersTalksDto(questionSetName, currentIndex, totalNumber, logoFileName,
                talks.map(Talk::getId),
                correctAnswerIds, yourAnswerIds,
                questionSpeakers,
                talks.map(t -> LocalizationUtils.getString(t.getName(), language)));
    }
}
