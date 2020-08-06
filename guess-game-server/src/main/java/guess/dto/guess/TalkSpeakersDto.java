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
public class TalkSpeakersDto extends EntitySpeakersDto {
    private final String talkName;

    public TalkSpeakersDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                           Quadruple<Long> ids, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                           Quadruple<String> speakerFileNames, Quadruple<String> speakerNames,
                           String talkName) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, ids, correctAnswerIds, yourAnswerIds,
                speakerFileNames, speakerNames);

        this.talkName = talkName;
    }

    public String getTalkName() {
        return talkName;
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
                speakers.map(Speaker::getPhotoFileName),
                names,
                LocalizationUtils.getString(((TalkQuestion) questionAnswers.getQuestion()).getTalk().getName(), language));
    }
}
