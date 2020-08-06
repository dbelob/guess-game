package guess.dto.guess;

import guess.domain.Quadruple;

import java.util.List;

/**
 * Entity, speakers DTO.
 */
public abstract class EntitySpeakersDto extends QuestionAnswersDto {
    private final Quadruple<String> speakerFileNames;
    private final Quadruple<String> speakerNames;

    public EntitySpeakersDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                              Quadruple<Long> ids, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                              Quadruple<String> speakerFileNames, Quadruple<String> speakerNames) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, ids, correctAnswerIds, yourAnswerIds);

        this.speakerFileNames = speakerFileNames;
        this.speakerNames = speakerNames;
    }

    public String getSpeakerFileName0() {
        return speakerFileNames.getFirst();
    }

    public String getSpeakerName0() {
        return speakerNames.getFirst();
    }

    public String getSpeakerFileName1() {
        return speakerFileNames.getSecond();
    }

    public String getSpeakerName1() {
        return speakerNames.getSecond();
    }

    public String getSpeakerFileName2() {
        return speakerFileNames.getThird();
    }

    public String getSpeakerName2() {
        return speakerNames.getThird();
    }

    public String getSpeakerFileName3() {
        return speakerFileNames.getFourth();
    }

    public String getSpeakerName3() {
        return speakerNames.getFourth();
    }
}
