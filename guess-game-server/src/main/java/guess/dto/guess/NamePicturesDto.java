package guess.dto.guess;

import guess.domain.Language;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.SpeakerQuestion;
import guess.util.LocalizationUtils;

import java.util.List;

/**
 * Name, pictures DTO.
 */
public class NamePicturesDto extends QuestionAnswersDto {
    private final String name;

    private final String fileName0;
    private final String fileName1;
    private final String fileName2;
    private final String fileName3;

    public NamePicturesDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                           long id0, long id1, long id2, long id3,
                           boolean invalid0, boolean invalid1, boolean invalid2, boolean invalid3,
                           String name, String fileName0, String fileName1, String fileName2, String fileName3) {
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

    public static NamePicturesDto convertToDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                                               QuestionAnswers questionAnswers, List<Long> wrongAnswerIds, Language language) {
        return new NamePicturesDto(questionSetName, currentIndex, totalNumber, logoFileName,
                questionAnswers.getAnswers().get(0).getId(), questionAnswers.getAnswers().get(1).getId(),
                questionAnswers.getAnswers().get(2).getId(), questionAnswers.getAnswers().get(3).getId(),
                wrongAnswerIds.contains(questionAnswers.getAnswers().get(0).getId()),
                wrongAnswerIds.contains(questionAnswers.getAnswers().get(1).getId()),
                wrongAnswerIds.contains(questionAnswers.getAnswers().get(2).getId()),
                wrongAnswerIds.contains(questionAnswers.getAnswers().get(3).getId()),
                LocalizationUtils.getString(((SpeakerQuestion) questionAnswers.getQuestion()).getSpeaker().getName(), language),
                ((SpeakerQuestion) questionAnswers.getAnswers().get(0)).getSpeaker().getFileName(),
                ((SpeakerQuestion) questionAnswers.getAnswers().get(1)).getSpeaker().getFileName(),
                ((SpeakerQuestion) questionAnswers.getAnswers().get(2)).getSpeaker().getFileName(),
                ((SpeakerQuestion) questionAnswers.getAnswers().get(3)).getSpeaker().getFileName());
    }
}
