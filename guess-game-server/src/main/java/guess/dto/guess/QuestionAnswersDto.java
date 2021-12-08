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
        this.questionSetName = sourceDto.questionSetName();
        this.currentIndex = sourceDto.currentIndex();
        this.totalNumber = sourceDto.totalNumber();
        this.logoFileName = sourceDto.logoFileName();
        this.ids = ids;
        this.invalid = ids.map(id -> isInvalid(sourceDto.correctAnswerIds(), sourceDto.yourAnswerIds(), id));
        this.valid = ids.map(id -> isValid(sourceDto.correctAnswerIds(), sourceDto.yourAnswerIds(), id));
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
        return ids.first();
    }

    public long getId1() {
        return ids.second();
    }

    public long getId2() {
        return ids.third();
    }

    public long getId3() {
        return ids.fourth();
    }

    public boolean isInvalid0() {
        return invalid.first();
    }

    public boolean isInvalid1() {
        return invalid.second();
    }

    public boolean isInvalid2() {
        return invalid.third();
    }

    public boolean isInvalid3() {
        return invalid.fourth();
    }

    public boolean isValid0() {
        return valid.first();
    }

    public boolean isValid1() {
        return valid.second();
    }

    public boolean isValid2() {
        return valid.third();
    }

    public boolean isValid3() {
        return valid.fourth();
    }
}
