package guess.dto;

/**
 * Speaker, talks DTO.
 */
public class SpeakerTalksDto extends QuestionAnswersDto {
    private String speakerFileName;
    private String speakerName;

    private String talkName0;
    private String talkName1;
    private String talkName2;
    private String talkName3;

    public SpeakerTalksDto(String questionSetName, int currentIndex, int totalNumber, String logoFileName,
                           String speakerFileName, String speakerName,
                           long id0, long id1, long id2, long id3,
                           String talkName0, String talkName1, String talkName2, String talkName3,
                           boolean invalid0, boolean invalid1, boolean invalid2, boolean invalid3) {
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
}
