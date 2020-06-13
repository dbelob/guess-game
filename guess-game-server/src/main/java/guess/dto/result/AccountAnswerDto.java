package guess.dto.result;

public class AccountAnswerDto {
    private final SpeakerPairDto speaker;
    private final String twitter;
    private final String gitHub;

    public AccountAnswerDto(SpeakerPairDto speaker, String twitter, String gitHub) {
        this.speaker = speaker;
        this.twitter = twitter;
        this.gitHub = gitHub;
    }

    public SpeakerPairDto getSpeaker() {
        return speaker;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getGitHub() {
        return gitHub;
    }
}
