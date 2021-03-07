package guess.domain.question;

import guess.domain.QuestionAnswer;
import guess.domain.source.Speaker;

import java.awt.image.BufferedImage;

/**
 * Question about tag cloud by speaker.
 */
public class TagCloudBySpeakerQuestion extends QuestionAnswer<Speaker> implements Question {
    private final BufferedImage tagCloudImage;

    protected TagCloudBySpeakerQuestion(BufferedImage tagCloudImage, Speaker speaker) {
        super(speaker);

        this.tagCloudImage = tagCloudImage;
    }

    public BufferedImage getTagCloudImage() {
        return tagCloudImage;
    }

    public Speaker getSpeaker() {
        return getEntity();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
