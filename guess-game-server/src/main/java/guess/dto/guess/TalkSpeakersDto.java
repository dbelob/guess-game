package guess.dto.guess;

import guess.domain.Language;
import guess.domain.Quadruple;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.TalkQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.Set;

/**
 * Talk, speakers DTO.
 */
public class TalkSpeakersDto extends QuestionAnswersDto {
    private final String talkName;
    private final Quadruple<String> speakerFileNames;
    private final Quadruple<String> speakerNames;

    public TalkSpeakersDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                           Quadruple<Long> ids, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                           String talkName,
                           Quadruple<String> speakerFileNames, Quadruple<String> speakerNames) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, ids, correctAnswerIds, yourAnswerIds);

        this.talkName = talkName;
        this.speakerFileNames = speakerFileNames;
        this.speakerNames = speakerNames;
    }

    public String getTalkName() {
        return talkName;
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

    public static TalkSpeakersDto convertToDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                                               QuestionAnswers questionAnswers, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                                               Language language) {
        Quadruple<Speaker> speakers =
                questionAnswers.getAvailableAnswers().map(
                        a -> ((SpeakerAnswer) a).getSpeaker()
                );
        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                speakers.asList(),
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);
        Quadruple<String> names =
                speakers.map(
                        s -> LocalizationUtils.getSpeakerName(s, language, speakerDuplicates)
                );

        return new TalkSpeakersDto(questionSetName, currentIndex, totalNumber, logoFileName,
                speakers.map(Speaker::getId),
                correctAnswerIds, yourAnswerIds,
                LocalizationUtils.getString(((TalkQuestion) questionAnswers.getQuestion()).getTalk().getName(), language),
                speakers.map(Speaker::getPhotoFileName),
                names);
    }
}
