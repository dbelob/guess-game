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
        return speakerPhotoFileNames.getFirst();
    }

    public String getSpeakerName0() {
        return speakerNames.getFirst();
    }

    public String getSpeakerPhotoFileName1() {
        return speakerPhotoFileNames.getSecond();
    }

    public String getSpeakerName1() {
        return speakerNames.getSecond();
    }

    public String getSpeakerPhotoFileName2() {
        return speakerPhotoFileNames.getThird();
    }

    public String getSpeakerName2() {
        return speakerNames.getThird();
    }

    public String getSpeakerPhotoFileName3() {
        return speakerPhotoFileNames.getFourth();
    }

    public String getSpeakerName3() {
        return speakerNames.getFourth();
    }
}
