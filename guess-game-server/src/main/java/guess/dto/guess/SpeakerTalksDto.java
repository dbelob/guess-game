package guess.dto.guess;

import guess.domain.Language;
import guess.domain.answer.TalkAnswer;
import guess.domain.question.QuestionAnswers;
import guess.domain.question.TalkQuestion;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.util.LocalizationUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                           long id0, long id1, long id2, long id3, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                           String speakerFileName, String speakerName,
                           String talkName0, String talkName1, String talkName2, String talkName3) {
        super(questionSetName, currentIndex, totalNumber, logoFileName, id0, id1, id2, id3, correctAnswerIds, yourAnswerIds);

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
                                               QuestionAnswers questionAnswers, List<Long> correctAnswerIds, List<Long> yourAnswerIds,
                                               Language language) {
        Speaker questionSpeaker = ((TalkQuestion) questionAnswers.getQuestion()).getSpeakers().get(0);  //TODO: is it right?
        Talk talk0 = ((TalkAnswer) questionAnswers.getAvailableAnswers().get(0)).getTalk();
        Talk talk1 = ((TalkAnswer) questionAnswers.getAvailableAnswers().get(1)).getTalk();
        Talk talk2 = ((TalkAnswer) questionAnswers.getAvailableAnswers().get(2)).getTalk();
        Talk talk3 = ((TalkAnswer) questionAnswers.getAvailableAnswers().get(3)).getTalk();
        Set<Speaker> talkSpeakers = new HashSet<Speaker>() {{
            addAll(talk0.getSpeakers());
            addAll(talk1.getSpeakers());
            addAll(talk2.getSpeakers());
            addAll(talk3.getSpeakers());
        }};

        Set<Speaker> speakerDuplicates = LocalizationUtils.getSpeakerDuplicates(
                new ArrayList<>(talkSpeakers),
                language,
                s -> LocalizationUtils.getString(s.getName(), language),
                s -> true);

        String questionName = LocalizationUtils.getSpeakerName(questionSpeaker, language, speakerDuplicates);

        return new SpeakerTalksDto(questionSetName, currentIndex, totalNumber, logoFileName,
                talk0.getId(), talk1.getId(), talk2.getId(), talk3.getId(),
                correctAnswerIds, yourAnswerIds,
                questionSpeaker.getFileName(),
                questionName,
                LocalizationUtils.getString(talk0.getName(), language),
                LocalizationUtils.getString(talk1.getName(), language),
                LocalizationUtils.getString(talk2.getName(), language),
                LocalizationUtils.getString(talk3.getName(), language));
    }
}
