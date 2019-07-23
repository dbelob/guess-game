package acme.guess.dto;

import acme.guess.domain.QuestionAnswers;

import java.util.Set;

/**
 * Name, pictures DTO.
 */
public class NamePicturesDto extends QuestionAnswersDto {
    private String name;

    private String fileName0;
    private String fileName1;
    private String fileName2;
    private String fileName3;

    public NamePicturesDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName, String name,
                           long id0, long id1, long id2, long id3,
                           String fileName0, String fileName1, String fileName2, String fileName3,
                           boolean invalid0, boolean invalid1, boolean invalid2, boolean invalid3) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, id0, id1, id2, id3, invalid0, invalid1, invalid2, invalid3);

        this.name = name;
        this.fileName0 = fileName0;
        this.fileName1 = fileName1;
        this.fileName2 = fileName2;
        this.fileName3 = fileName3;
    }

    public String getName() {
        return name;
    }

    public String getFileName0() {
        return fileName0;
    }

    public String getFileName1() {
        return fileName1;
    }

    public String getFileName2() {
        return fileName2;
    }

    public String getFileName3() {
        return fileName3;
    }

    public static NamePicturesDto convertToDto(String questionSetName, int currentNumber, int totalNumber, String directoryName,
                                               String logoFileName, QuestionAnswers questionAnswers, Set<Long> invalidAnswerIds) {
        return new NamePicturesDto(questionSetName, currentNumber, totalNumber,
                (logoFileName != null) ? String.format("%s/%s", directoryName, logoFileName) : null,
                questionAnswers.getQuestion().getName(),
                questionAnswers.getAnswers().get(0).getId(), questionAnswers.getAnswers().get(1).getId(),
                questionAnswers.getAnswers().get(2).getId(), questionAnswers.getAnswers().get(3).getId(),
                String.format("%s/%s", directoryName, questionAnswers.getAnswers().get(0).getFileName()),
                String.format("%s/%s", directoryName, questionAnswers.getAnswers().get(1).getFileName()),
                String.format("%s/%s", directoryName, questionAnswers.getAnswers().get(2).getFileName()),
                String.format("%s/%s", directoryName, questionAnswers.getAnswers().get(3).getFileName()),
                invalidAnswerIds.contains(questionAnswers.getAnswers().get(0).getId()),
                invalidAnswerIds.contains(questionAnswers.getAnswers().get(1).getId()),
                invalidAnswerIds.contains(questionAnswers.getAnswers().get(2).getId()),
                invalidAnswerIds.contains(questionAnswers.getAnswers().get(3).getId()));
    }
}
