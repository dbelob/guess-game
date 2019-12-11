package guess.dto.guess;

import guess.domain.Language;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.TalkQuestion;
import guess.util.LocalizationUtils;

import java.util.List;

/**
 * Speaker, talks DTO.
 */
public class SpeakerTalksDto extends QuestionAnswersDto {
    private final String speakerFileName;
    private final String speakerName;

    private final String talkName0;
    private final String talkName1;
    private final String talkName2;
    private final String talkName3;

    public SpeakerTalksDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                           long id0, long id1, long id2, long id3,
                           boolean invalid0, boolean invalid1, boolean invalid2, boolean invalid3,
                           String speakerFileName, String speakerName,
                           String talkName0, String talkName1, String talkName2, String talkName3) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, id0, id1, id2, id3, invalid0, invalid1, invalid2, invalid3);

        this.speakerFileName = speakerFileName;
        this.speakerName = speakerName;
        this.talkName0 = talkName0;
        this.talkName1 = talkName1;
        this.talkName2 = talkName2;
        this.talkName3 = talkName3;
    }

    public String getSpeakerFileName() {
        return speakerFileName;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public String getTalkName0() {
        return talkName0;
    }

    public String getTalkName1() {
        return talkName1;
    }

    public String getTalkName2() {
        return talkName2;
    }

    public String getTalkName3() {
        return talkName3;
    }

    public static SpeakerTalksDto convertToDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                                               QuestionAnswers questionAnswers, List<Long> wrongAnswerIds, Language language) {
        return new SpeakerTalksDto(questionSetName, currentIndex, totalNumber, logoFileName,
                questionAnswers.getAnswers().get(0).getId(), questionAnswers.getAnswers().get(1).getId(),
                questionAnswers.getAnswers().get(2).getId(), questionAnswers.getAnswers().get(3).getId(),
                wrongAnswerIds.contains(questionAnswers.getAnswers().get(0).getId()),
                wrongAnswerIds.contains(questionAnswers.getAnswers().get(1).getId()),
                wrongAnswerIds.contains(questionAnswers.getAnswers().get(2).getId()),
                wrongAnswerIds.contains(questionAnswers.getAnswers().get(3).getId()),
                ((TalkQuestion) questionAnswers.getQuestion()).getSpeaker().getFileName(),
                LocalizationUtils.getString(((TalkQuestion) questionAnswers.getQuestion()).getSpeaker().getName(), language),
                LocalizationUtils.getString(((TalkQuestion) questionAnswers.getAnswers().get(0)).getTalk().getName(), language),
                LocalizationUtils.getString(((TalkQuestion) questionAnswers.getAnswers().get(1)).getTalk().getName(), language),
                LocalizationUtils.getString(((TalkQuestion) questionAnswers.getAnswers().get(2)).getTalk().getName(), language),
                LocalizationUtils.getString(((TalkQuestion) questionAnswers.getAnswers().get(3)).getTalk().getName(), language));
    }
}
