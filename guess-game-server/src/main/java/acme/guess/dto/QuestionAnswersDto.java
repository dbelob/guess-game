package acme.guess.dto;

/**
 * Question, answers DTO.
 */
public abstract class QuestionAnswersDto {
    private String questionSetName;
    private int currentIndex;
    private int totalNumber;
    private String logoFileName;

    private long id0;
    private long id1;
    private long id2;
    private long id3;

    private boolean invalid0;
    private boolean invalid1;
    private boolean invalid2;
    private boolean invalid3;

    public QuestionAnswersDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                              long id0, long id1, long id2, long id3,
                              boolean invalid0, boolean invalid1, boolean invalid2, boolean invalid3) {
        this.questionSetName = questionSetName;
        this.currentIndex = currentIndex;
        this.totalNumber = totalNumber;
        this.logoFileName = logoFileName;
        this.id0 = id0;
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.invalid0 = invalid0;
        this.invalid1 = invalid1;
        this.invalid2 = invalid2;
        this.invalid3 = invalid3;
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
}
