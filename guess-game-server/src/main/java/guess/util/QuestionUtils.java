package guess.util;

import guess.domain.Identifiable;

import java.util.*;

/**
 * Question utility methods.
 */
public class QuestionUtils {
    private QuestionUtils() {
    }

    public static <T extends Identifiable> List<T> removeDuplicatesById(List<T> items) {
        List<T> result = new ArrayList<>();
        Set<Long> ids = new HashSet<>();

        if (items != null) {
            for (T item : items) {
                if (!ids.contains(item.getId())) {
                    result.add(item);
                    ids.add(item.getId());
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
