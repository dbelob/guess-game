package guess.dto.guess;

import java.util.List;

/**
 * Question, answers DTO.
 */
public abstract class QuestionAnswersDto {
    private final String questionSetName;
    private final int currentIndex;
    private final int totalNumber;
    private final String logoFileName;

    private final long id0;
    private final long id1;
    private final long id2;
    private final long id3;

    private final boolean invalid0;
    private final boolean invalid1;
    private final boolean invalid2;
    private final boolean invalid3;

    private final boolean valid0;
    private final boolean valid1;
    private final boolean valid2;
    private final boolean valid3;

    public QuestionAnswersDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                              long id0, long id1, long id2, long id3, List<Long> correctAnswerIds, List<Long> yourAnswerIds) {
        this.questionSetName = questionSetName;
        this.currentIndex = currentIndex;
        this.totalNumber = totalNumber;
        this.logoFileName = logoFileName;
        this.id0 = id0;
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.invalid0 = isInvalid(correctAnswerIds, yourAnswerIds, id0);
        this.invalid1 = isInvalid(correctAnswerIds, yourAnswerIds, id1);
        this.invalid2 = isInvalid(correctAnswerIds, yourAnswerIds, id2);
        this.invalid3 = isInvalid(correctAnswerIds, yourAnswerIds, id3);
        this.valid0 = isValid(correctAnswerIds, yourAnswerIds, id0);
        this.valid1 = isValid(correctAnswerIds, yourAnswerIds, id1);
        this.valid2 = isValid(correctAnswerIds, yourAnswerIds, id2);
        this.valid3 = isValid(correctAnswerIds, yourAnswerIds, id3);
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
        return id0;
    }

    public long getId1() {
        return id1;
    }

    public long getId2() {
        return id2;
    }

    public long getId3() {
        return id3;
    }

    public boolean isInvalid0() {
        return invalid0;
    }

    public boolean isInvalid1() {
        return invalid1;
    }

    public boolean isInvalid2() {
        return invalid2;
    }

    public boolean isInvalid3() {
        return invalid3;
    }

    public boolean isValid0() {
        return valid0;
    }

    public boolean isValid1() {
        return valid1;
    }

    public boolean isValid2() {
        return valid2;
    }

    public boolean isValid3() {
        return valid3;
    }
}
