package guess.dto.guess;

import guess.domain.Language;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
                           long id0, long id1, long id2, long id3, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                           String name, String fileName0, String fileName1, String fileName2, String fileName3) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, id0, id1, id2, id3, correctAnswerIds, yourAnswerIds);

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
                                               QuestionAnswers questionAnswers, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                                               Language language) {
        Speaker questionSpeaker = ((SpeakerQuestion) questionAnswers.getQuestion()).getSpeaker();
        Speaker answerSpeaker0 = ((SpeakerAnswer) questionAnswers.getAvailableAnswers().getFirst()).getSpeaker();
        Speaker answerSpeaker1 = ((SpeakerAnswer) questionAnswers.getAvailableAnswers().getSecond()).getSpeaker();
        Speaker answerSpeaker2 = ((SpeakerAnswer) questionAnswers.getAvailableAnswers().getThird()).getSpeaker();
        Speaker answerSpeaker3 = ((SpeakerAnswer) questionAnswers.getAvailableAnswers().getFourth()).getSpeaker();

        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                Arrays.asList(answerSpeaker0, answerSpeaker1, answerSpeaker2, answerSpeaker3),
                language,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);

        String questionName = LocalizationUtils.getSpeakerName(questionSpeaker, language, speakerDuplicates);

        return new NamePicturesDto(questionSetName, currentIndex, totalNumber, logoFileName,
                answerSpeaker0.getId(), answerSpeaker1.getId(), answerSpeaker2.getId(), answerSpeaker3.getId(),
                correctAnswerIds, yourAnswerIds,
                questionName,
                answerSpeaker0.getFileName(),
                answerSpeaker1.getFileName(),
                answerSpeaker2.getFileName(),
                answerSpeaker3.getFileName());
    }
}
