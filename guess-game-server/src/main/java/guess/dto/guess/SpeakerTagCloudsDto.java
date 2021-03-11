package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.TagCloudAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.TagCloudQuestion;
import guess.domain.source.Speaker;
import guess.dto.result.SpeakerPairDto;
import guess.util.LocalizationUtils;

/**
 * Speaker, tag clouds DTO.
 */
public class SpeakerTagCloudsDto extends QuestionAnswersDto {
    private final SpeakerPairDto speaker;
    //TODO: add tag cloud image

    public SpeakerTagCloudsDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids, SpeakerPairDto speaker) {
        super(sourceDto, ids);

        this.speaker = speaker;
    }

    public SpeakerPairDto getSpeaker() {
        return speaker;
    }

    public static SpeakerTagCloudsDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
                                                   Language language) {
        Quadruple<Speaker> speakers =
                questionAnswers.getAvailableAnswers().map(
                        a -> ((TagCloudAnswer) a).getSpeaker()
                );
        Speaker questionSpeaker = ((TagCloudQuestion) questionAnswers.getQuestion()).getSpeaker();

        return new SpeakerTagCloudsDto(
                sourceDto,
                speakers.map(Speaker::getId),
                new SpeakerPairDto(
                        LocalizationUtils.getString(questionSpeaker.getName(), language),
                        questionSpeaker.getPhotoFileName()));
    }
}
