package guess.dto.guess;

import guess.domain.Quadruple;

import java.util.List;

/**
 * Question, answers DTO.
 */
public abstract class QuestionAnswersDto {
    private final String questionSetName;
    private final int currentIndex;
    private final int totalNumber;
    private final String logoFileName;

    private final Quadruple<Long> ids;
    private final Quadruple<Boolean> invalid;
    private final Quadruple<Boolean> valid;

    protected QuestionAnswersDto(QuestionAnswersSourceDto sourceDto, Quadruple<Long> ids) {
        this.questionSetName = sourceDto.getQuestionSetName();
        this.currentIndex = sourceDto.getCurrentIndex();
        this.totalNumber = sourceDto.getTotalNumber();
        this.logoFileName = sourceDto.getLogoFileName();
        this.ids = ids;
        this.invalid = ids.map(id -> isInvalid(sourceDto.getCorrectAnswerIds(), sourceDto.getYourAnswerIds(), id));
        this.valid = ids.map(id -> isValid(sourceDto.getCorrectAnswerIds(), sourceDto.getYourAnswerIds(), id));
    }

    private boolean isInvalid(List<Long> correctAnswerIds, List<Long> yourAnswerIds, long id) {
        return yourAnswerIds.contains(id) && !correctAnswerIds.contains(id);
    }

    private boolean isValid(List<Long> correctAnswerIds, List<Long> yourAnswerIds, long id) {
        return yourAnswerIds.contains(id) && correctAnswerIds.contains(id);
    }

    public String getQuestionSetName() {
        return questionSetName;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public long getId0() {
        return ids.getFirst();
    }

    public long getId1() {
        return ids.getSecond();
    }

    public long getId2() {
        return ids.getThird();
    }

    public long getId3() {
        return ids.getFourth();
    }

    public boolean isInvalid0() {
        return invalid.getFirst();
    }

    public boolean isInvalid1() {
        return invalid.getSecond();
    }

    public boolean isInvalid2() {
        return invalid.getThird();
    }

    public boolean isInvalid3() {
        return invalid.getFourth();
    }

    public boolean isValid0() {
        return valid.getFirst();
    }

    public boolean isValid1() {
        return valid.getSecond();
    }

    public boolean isValid2() {
        return valid.getThird();
    }

    public boolean isValid3() {
        return valid.getFourth();
    }
}
