package guess.util;

import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;

import java.util.*;

/**
 * Question utility methods.
 */
public class QuestionUtils {
    public static List<SpeakerQuestion> removeDuplicatesByFileName(List<SpeakerQuestion> speakerQuestions) {
        List<SpeakerQuestion> result = new ArrayList<>();
        Set<String> fileNames = new HashSet<>();

        if (speakerQuestions != null) {
            for (SpeakerQuestion speakerQuestion : speakerQuestions) {
                if (!fileNames.contains(speakerQuestion.getFileName())) {
                    result.add(speakerQuestion);
                    fileNames.add(speakerQuestion.getFileName());
                }
            }
        }

        return result;
    }

    public static List<TalkQuestion> removeDuplicatesById(List<TalkQuestion> talkQuestions) {
        List<TalkQuestion> result = new ArrayList<>();
        Set<Long> ids = new HashSet<>();

        if (talkQuestions != null) {
            for (TalkQuestion talkQuestion : talkQuestions) {
                if (!ids.contains(talkQuestion.getId())) {
                    result.add(talkQuestion);
                    ids.add(talkQuestion.getId());
                }
            }
        }

        return result;
    }

    public static List<Integer> getQuantities(int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<>();

        // Add 5, 10, 20, 50
        for (int i : Arrays.asList(5, 10, 20, 50)) {
            if (i <= count) {
                result.add(i);
            }
        }

        // Add 100, 200, 300 etc.
        for (int i = 1; i <= (count / 100); i++) {
            result.add(i * 100);
        }

        // Add itself
        if (!result.contains(count)) {
            result.add(count);
        }

        return result;
    }
}
