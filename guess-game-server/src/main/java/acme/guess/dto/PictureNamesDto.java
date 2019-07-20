package acme.guess.dto;

import acme.guess.domain.QuestionAnswers;

/**
 * Picture, names DTO.
 */
public class PictureNamesDto {
    private String questionSetName;
    private int currentNumber;
    private int totalNumber;

    private String fileName;

    private long id0;
    private long id1;
    private long id2;
    private long id3;
    private String name0;
    private String name1;
    private String name2;
    private String name3;

    public PictureNamesDto(String questionSetName, int currentNumber, int totalNumber, String fileName, long id0, long id1, long id2, long id3, String name0, String name1, String name2, String name3) {
        this.questionSetName = questionSetName;
        this.currentNumber = currentNumber;
        this.totalNumber = totalNumber;
        this.fileName = fileName;
        this.id0 = id0;
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.name0 = name0;
        this.name1 = name1;
        this.name2 = name2;
        this.name3 = name3;
    }

    public String getQuestionSetName() {
        return questionSetName;
    }

    public int getCurrentNumber() {
        return currentNumber;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public String getFileName() {
        return fileName;
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

    public String getName0() {
        return name0;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }

    public String getName3() {
        return name3;
    }

    public static PictureNamesDto convertToDto(String questionSetName, int currentNumber, int totalNumber, String directoryName, QuestionAnswers questionAnswers) {

        return new PictureNamesDto(questionSetName, currentNumber, totalNumber,
                String.format("%s/%s", directoryName, questionAnswers.getQuestion().getFileName()),
                questionAnswers.getAnswers().get(0).getId(), questionAnswers.getAnswers().get(1).getId(),
                questionAnswers.getAnswers().get(2).getId(), questionAnswers.getAnswers().get(3).getId(),
                questionAnswers.getAnswers().get(0).getName(), questionAnswers.getAnswers().get(1).getName(),
                questionAnswers.getAnswers().get(2).getName(), questionAnswers.getAnswers().get(3).getName());
    }
}
