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

/**
 * Speakers, talks DTO.
 */
public class SpeakersTalksDto extends QuestionAnswersDto {
    private final List<SpeakerPairDto> speakers;
    private final Quadruple<String> talkNames;

    public SpeakersTalksDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids, List<SpeakerPairDto> speakers,
                            Quadruple<String> talkNames) {
        super(sourceDto, ids);

        this.speakers = speakers;
        this.talkNames = talkNames;
    }

    public List<SpeakerPairDto> getSpeakers() {
        return speakers;
    }

    public String getTalkName0() {
        return talkNames.first();
    }

    public String getTalkName1() {
        return talkNames.second();
    }

    public String getTalkName2() {
        return talkNames.third();
    }

    public String getTalkName3() {
        return talkNames.fourth();
    }

    public static SpeakersTalksDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
                                                Language language) {
        Quadruple<Talk> talks =
                questionAnswers.availableAnswers().map(
                        a -> ((TalkAnswer) a).getTalk()
                );

        Set<Speaker> talkSpeakers = new HashSet<>();
        talkSpeakers.addAll(talks.first().getSpeakers());
        talkSpeakers.addAll(talks.second().getSpeakers());
        talkSpeakers.addAll(talks.third().getSpeakers());
        talkSpeakers.addAll(talks.fourth().getSpeakers());

        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                new ArrayList<>(talkSpeakers),
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);
        List<SpeakerPairDto> questionSpeakers = ((TalkQuestion) questionAnswers.question()).getSpeakers().stream()
                .map(s -> new SpeakerPairDto(
                        LocalizationUtils.getSpeakerName(s, language, speakerDuplicates),
                        s.getPhotoFileName()))
                .toList();

        return new SpeakersTalksDto(
                sourceDto,
                talks.map(Talk::getId),
                questionSpeakers,
                talks.map(t -> LocalizationUtils.getString(t.getName(), language)));
    }
}
