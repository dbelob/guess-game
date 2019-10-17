package guess.domain.answer;

import guess.domain.GuessType;

/**
 * Result.
 */
public class Result {
    private long correctAnswers;
    private long wrongAnswers;
    private long skippedAnswers;
    private float correctPercents;
    private float wrongPercents;
    private float skippedPercents;
    private GuessType guessType;

    public Result(long correctAnswers, long wrongAnswers, long skippedAnswers,
                  float correctPercents, float wrongPercents, float skippedPercents,
                  GuessType guessType) {
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.skippedAnswers = skippedAnswers;
        this.correctPercents = correctPercents;
        this.wrongPercents = wrongPercents;
        this.skippedPercents = skippedPercents;
        this.guessType = guessType;
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

    public GuessType getGuessType() {
        return guessType;
    }
}
