package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.TalkQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.Set;

/**
 * Talk, speakers DTO.
 */
public class TalkSpeakersDto extends EntitySpeakersDto {
    private final String talkName;

    public TalkSpeakersDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids,
                           Quadruple<String> speakerPhotoFileNames, Quadruple<String> speakerNames,
                           String talkName) {
        super(sourceDto, ids, speakerPhotoFileNames, speakerNames);

        this.talkName = talkName;
    }

    public String getTalkName() {
        return talkName;
    }

    public static TalkSpeakersDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
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

        return new TalkSpeakersDto(
                sourceDto,
                speakers.map(Speaker::getId),
                speakers.map(Speaker::getPhotoFileName),
                names,
                LocalizationUtils.getString(((TalkQuestion) questionAnswers.question()).getTalk().getName(), language));
    }
}
