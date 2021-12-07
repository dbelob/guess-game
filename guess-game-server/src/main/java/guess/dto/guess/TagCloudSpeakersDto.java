package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.TagCloudQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;
import guess.util.tagcloud.TagCloudUtils;

import java.util.Set;

/**
 * Tag cloud, speakers DTO.
 */
public class TagCloudSpeakersDto extends EntitySpeakersDto {
    private final byte[] image;

    public TagCloudSpeakersDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids,
                               Quadruple<String> speakerPhotoFileNames, Quadruple<String> speakerNames,
                               byte[] image) {
        super(sourceDto, ids, speakerPhotoFileNames, speakerNames);

        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public static TagCloudSpeakersDto convertToDto(QuestionAnswersSourceDto sourceDto, QuestionAnswers questionAnswers,
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
        TagCloudQuestion question = (TagCloudQuestion) questionAnswers.question();

        return new TagCloudSpeakersDto(
                sourceDto,
                speakers.map(Speaker::getId),
                speakers.map(Speaker::getPhotoFileName),
                names,
                TagCloudUtils.getImage(question.getLanguageImageMap(), language));
    }
}
