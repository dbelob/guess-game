package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.TagCloudAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.TagCloudQuestion;
import guess.domain.source.Speaker;
import guess.dto.result.SpeakerPairDto;
import guess.util.LocalizationUtils;
import guess.util.tagcloud.TagCloudUtils;

/**
 * Speaker, tag clouds DTO.
 */
public class SpeakerTagCloudsDto extends QuestionAnswersDto {
    private final SpeakerPairDto speaker;
    private final Quadruple<byte[]> images;

    public SpeakerTagCloudsDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids, SpeakerPairDto speaker,
                               Quadruple<byte[]> images) {
        super(sourceDto, ids);

        this.speaker = speaker;
        this.images = images;
    }

    public SpeakerPairDto getSpeaker() {
        return speaker;
    }

    public byte[] getImage0() {
        return images.getFirst();
    }

    public byte[] getImage1() {
        return images.getSecond();
    }

    public byte[] getImage2() {
        return images.getThird();
    }

    public byte[] getImage3() {
        return images.getFourth();
    }

    public static SpeakerTagCloudsDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
                                                   Language language) {
        Quadruple<TagCloudAnswer> answers =
                questionAnswers.getAvailableAnswers().map(
                        a -> (TagCloudAnswer) a
                );
        Speaker questionSpeaker = ((TagCloudQuestion) questionAnswers.getQuestion()).getSpeaker();

        return new SpeakerTagCloudsDto(
                sourceDto,
                answers.map(TagCloudAnswer::getId),
                new SpeakerPairDto(
                        LocalizationUtils.getString(questionSpeaker.getName(), language),
                        questionSpeaker.getPhotoFileName()),
                answers.map(a -> TagCloudUtils.getImage(a.getLanguageImageMap(), language))
        );
    }
}
