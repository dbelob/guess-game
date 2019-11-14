package guess.util;

import guess.domain.question.Question;

import java.util.*;

/**
 * Question utility methods.
 */
public class QuestionUtils {
    public static <T extends Question> List<T> removeDuplicatesById(List<T> questions) {
        List<T> result = new ArrayList<>();
        Set<Long> ids = new HashSet<>();

        if (questions != null) {
            for (T question : questions) {
                if (!ids.contains(question.getId())) {
                    result.add(question);
                    ids.add(question.getId());
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
