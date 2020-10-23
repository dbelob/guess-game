package guess.domain.answer;

import guess.domain.GuessMode;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Result)) return false;
        Result result = (Result) o;
        return correctAnswers == result.correctAnswers &&
                wrongAnswers == result.wrongAnswers &&
                skippedAnswers == result.skippedAnswers &&
                Float.compare(result.correctPercents, correctPercents) == 0 &&
                Float.compare(result.wrongPercents, wrongPercents) == 0 &&
                Float.compare(result.skippedPercents, skippedPercents) == 0 &&
                guessMode == result.guessMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(correctAnswers, wrongAnswers, skippedAnswers, correctPercents, wrongPercents, skippedPercents, guessMode);
    }

    @Override
    public String toString() {
        return "Result{" +
                "correctAnswers=" + correctAnswers +
                ", wrongAnswers=" + wrongAnswers +
                ", skippedAnswers=" + skippedAnswers +
                ", correctPercents=" + correctPercents +
                ", wrongPercents=" + wrongPercents +
                ", skippedPercents=" + skippedPercents +
                ", guessMode=" + guessMode +
                '}';
    }
}
