package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.TagCloudAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.TagCloudQuestion;
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
        return images.first();
    }

    public byte[] getImage1() {
        return images.second();
    }

    public byte[] getImage2() {
        return images.third();
    }

    public byte[] getImage3() {
        return images.fourth();
    }

    public static SpeakerTagCloudsDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
                                                   Language language) {
        Quadruple<TagCloudAnswer> answers =
                questionAnswers.availableAnswers().map(TagCloudAnswer.class::cast);
        var questionSpeaker = ((TagCloudQuestion) questionAnswers.question()).getSpeaker();

        return new SpeakerTagCloudsDto(
                sourceDto,
                answers.map(TagCloudAnswer::getId),
                new SpeakerPairDto(
                        LocalizationUtils.getString(questionSpeaker.getName(), language),
                        questionSpeaker.getPhotoFileName()),
                answers.parallelMap(a -> TagCloudUtils.getImage(a.getLanguageImageMap(), language))
        );
    }
}
