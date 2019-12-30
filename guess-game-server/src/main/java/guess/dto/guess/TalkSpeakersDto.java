package guess.dto.guess;

import guess.domain.Language;
import guess.domain.answer.SpeakerAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.TalkQuestion;
import guess.domain.source.Speaker;
import guess.util.LocalizationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Talk, speakers DTO.
 */
public class TalkSpeakersDto extends QuestionAnswersDto {
    private final String talkName;

    private final String speakerFileName0;
    private final String speakerName0;
    private final String speakerFileName1;
    private final String speakerName1;
    private final String speakerFileName2;
    private final String speakerName2;
    private final String speakerFileName3;
    private final String speakerName3;

    public TalkSpeakersDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                           long id0, long id1, long id2, long id3,
                           boolean invalid0, boolean invalid1, boolean invalid2, boolean invalid3,
                           String talkName,
                           String speakerFileName0, String speakerName0, String speakerFileName1, String speakerName1,
                           String speakerFileName2, String speakerName2, String speakerFileName3, String speakerName3) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, id0, id1, id2, id3, invalid0, invalid1, invalid2, invalid3);

        this.talkName = talkName;
        this.speakerFileName0 = speakerFileName0;
        this.speakerName0 = speakerName0;
        this.speakerFileName1 = speakerFileName1;
        this.speakerName1 = speakerName1;
        this.speakerFileName2 = speakerFileName2;
        this.speakerName2 = speakerName2;
        this.speakerFileName3 = speakerFileName3;
        this.speakerName3 = speakerName3;
    }

    public String getTalkName() {
        return talkName;
    }

    public String getSpeakerFileName0() {
        return speakerFileName0;
    }

    public String getSpeakerName0() {
        return speakerName0;
    }

    public String getSpeakerFileName1() {
        return speakerFileName1;
    }

    public String getSpeakerName1() {
        return speakerName1;
    }

    public String getSpeakerFileName2() {
        return speakerFileName2;
    }

    public String getSpeakerName2() {
        return speakerName2;
    }

    public String getSpeakerFileName3() {
        return speakerFileName3;
    }

    public String getSpeakerName3() {
        return speakerName3;
    }

    public static TalkSpeakersDto convertToDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                                               QuestionAnswers questionAnswers, List<Long> wrongAnswerIds, Language language) {
        Speaker speaker0 = ((SpeakerAnswer) questionAnswers.getAvailableAnswers().get(0)).getSpeaker();
        Speaker speaker1 = ((SpeakerAnswer) questionAnswers.getAvailableAnswers().get(1)).getSpeaker();
        Speaker speaker2 = ((SpeakerAnswer) questionAnswers.getAvailableAnswers().get(2)).getSpeaker();
        Speaker speaker3 = ((SpeakerAnswer) questionAnswers.getAvailableAnswers().get(3)).getSpeaker();

        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                Arrays.asList(speaker0, speaker1, speaker2, speaker3),
                language,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);

        String name0 = LocalizationUtils.getSpeakerName(speaker0, language, speakerDuplicates);
        String name1 = LocalizationUtils.getSpeakerName(speaker1, language, speakerDuplicates);
        String name2 = LocalizationUtils.getSpeakerName(speaker2, language, speakerDuplicates);
        String name3 = LocalizationUtils.getSpeakerName(speaker3, language, speakerDuplicates);

        return new TalkSpeakersDto(questionSetName, currentIndex, totalNumber, logoFileName,
                speaker0.getId(), speaker1.getId(), speaker2.getId(), speaker3.getId(),
                wrongAnswerIds.contains(speaker0.getId()),
                wrongAnswerIds.contains(speaker1.getId()),
                wrongAnswerIds.contains(speaker2.getId()),
                wrongAnswerIds.contains(speaker3.getId()),
                LocalizationUtils.getString(((TalkQuestion) questionAnswers.getQuestion()).getTalk().getName(), language),
                speaker0.getFileName(),
                name0,
                speaker1.getFileName(),
                name1,
                speaker2.getFileName(),
                name2,
                speaker3.getFileName(),
                name3);
    }
}
