package guess.dto.guess;

import guess.domain.Quadruple;

/**
 * Entity, speakers DTO.
 */
public abstract class EntitySpeakersDto extends QuestionAnswersDto {
    private final Quadruple<String> speakerPhotoFileNames;
    private final Quadruple<String> speakerNames;

    protected EntitySpeakersDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids, Quadruple<String> speakerPhotoFileNames,
                                Quadruple<String> speakerNames) {
        super(sourceDto, ids);

        this.speakerPhotoFileNames = speakerPhotoFileNames;
        this.speakerNames = speakerNames;
    }

    public String getSpeakerPhotoFileName0() {
        return speakerPhotoFileNames.first();
    }

    public String getSpeakerName0() {
        return speakerNames.first();
    }

    public String getSpeakerPhotoFileName1() {
        return speakerPhotoFileNames.second();
    }

    public String getSpeakerName1() {
        return speakerNames.second();
    }

    public String getSpeakerPhotoFileName2() {
        return speakerPhotoFileNames.third();
    }

    public String getSpeakerName2() {
        return speakerNames.third();
    }

    public String getSpeakerPhotoFileName3() {
        return speakerPhotoFileNames.fourth();
    }

    public String getSpeakerName3() {
        return speakerNames.fourth();
    }
}
