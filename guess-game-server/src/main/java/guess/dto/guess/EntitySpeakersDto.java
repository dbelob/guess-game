package guess.dto.guess;

import guess.domain.Quadruple;

import java.util.List;

/**
 * Entity, speakers DTO.
 */
public abstract class EntitySpeakersDto extends QuestionAnswersDto {
    private final Quadruple<String> speakerPhotoFileNames;
    private final Quadruple<String> speakerNames;

    public EntitySpeakersDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                             Quadruple<Long> ids, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                             Quadruple<String> speakerPhotoFileNames, Quadruple<String> speakerNames) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, ids, correctAnswerIds, yourAnswerIds);

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
