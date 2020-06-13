package guess.domain.answer;

import guess.domain.GuessMode;

/**
 * Result.
 */
public class Result {
    private final long correctAnswers;
    private final long wrongAnswers;
    private final long skippedAnswers;
    private final float correctPercents;
    private final float wrongPercents;
    private final float skippedPercents;
    private final GuessMode guessMode;

    public Result(long correctAnswers, long wrongAnswers, long skippedAnswers,
                  float correctPercents, float wrongPercents, float skippedPercents,
                  GuessMode guessMode) {
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.skippedAnswers = skippedAnswers;
        this.correctPercents = correctPercents;
        this.wrongPercents = wrongPercents;
        this.skippedPercents = skippedPercents;
        this.guessMode = guessMode;
    }

    public long getCorrectAnswers() {
        return correctAnswers;
    }

    public long getWrongAnswers() {
        return wrongAnswers;
    }

    public long getSkippedAnswers() {
        return skippedAnswers;
    }

    public float getCorrectPercents() {
        return correctPercents;
    }

    public float getWrongPercents() {
        return wrongPercents;
    }

    public float getSkippedPercents() {
        return skippedPercents;
    }

    public GuessMode getGuessMode() {
        return guessMode;
    }
}
