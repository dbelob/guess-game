package guess.util;

import guess.domain.Identifiable;
import guess.domain.question.SpeakerQuestion;
import guess.domain.source.Speaker;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(Enclosed.class)
public class QuestionUtilsTest {
    @RunWith(Parameterized.class)
    public static class RemoveDuplicatesByIdTest {
        private static Identifiable createIdentifiable(long id) {
            Speaker speaker = new Speaker();
            speaker.setId(id);

            return new SpeakerQuestion(speaker);
        }

        @Parameters
        public static Collection<Object[]> data() {
            Identifiable identifiable0 = createIdentifiable(0);
            Identifiable identifiable1 = createIdentifiable(1);
            Identifiable identifiable2 = createIdentifiable(2);

            return Arrays.asList(new Object[][]{
                    {null, Collections.emptyList()},
                    {Collections.emptyList(), Collections.emptyList()},
                    {Collections.singletonList(identifiable0), Collections.singletonList(identifiable0)},
                    {Arrays.asList(identifiable0, identifiable0), Collections.singletonList(identifiable0)},
                    {Arrays.asList(identifiable0, identifiable1), Arrays.asList(identifiable0, identifiable1)},
                    {Arrays.asList(identifiable0, identifiable1, identifiable0), Arrays.asList(identifiable0, identifiable1)},
                    {Arrays.asList(identifiable0, identifiable1, identifiable0, identifiable1), Arrays.asList(identifiable0, identifiable1)},
                    {Arrays.asList(identifiable0, identifiable1, identifiable2), Arrays.asList(identifiable0, identifiable1, identifiable2)},
                    {Arrays.asList(identifiable0, identifiable1, identifiable2, identifiable0), Arrays.asList(identifiable0, identifiable1, identifiable2)},
                    {Arrays.asList(identifiable0, identifiable1, identifiable2, identifiable0, identifiable1), Arrays.asList(identifiable0, identifiable1, identifiable2)}
            });
        }

        private final List<? extends Identifiable> items;
        private final List<? extends Identifiable> expected;

        public RemoveDuplicatesByIdTest(List<? extends Identifiable> items, List<? extends Identifiable> expected) {
            this.items = items;
            this.expected = expected;
        }

        @Test
        public void removeDuplicatesById() {
            assertEquals(expected, QuestionUtils.removeDuplicatesById(items));
        }
    }

    @RunWith(Parameterized.class)
    public static class GetQuantitiesTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {0, Collections.emptyList()},
                    {1, Collections.singletonList(1)},
                    {2, Collections.singletonList(2)},
                    {3, Collections.singletonList(3)},
                    {4, Collections.singletonList(4)},
                    {5, Collections.singletonList(5)},
                    {10, Arrays.asList(5, 10)},
                    {11, Arrays.asList(5, 10, 11)},
                    {20, Arrays.asList(5, 10, 20)},
                    {25, Arrays.asList(5, 10, 20, 25)},
                    {50, Arrays.asList(5, 10, 20, 50)},
                    {57, Arrays.asList(5, 10, 20, 50, 57)},
                    {100, Arrays.asList(5, 10, 20, 50, 100)},
                    {105, Arrays.asList(5, 10, 20, 50, 100, 105)},
                    {200, Arrays.asList(5, 10, 20, 50, 100, 200)},
                    {242, Arrays.asList(5, 10, 20, 50, 100, 200, 242)},
                    {300, Arrays.asList(5, 10, 20, 50, 100, 200, 300)},
                    {383, Arrays.asList(5, 10, 20, 50, 100, 200, 300, 383)},
                    {1000, Arrays.asList(5, 10, 20, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000)},
                    {1001, Arrays.asList(5, 10, 20, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1001)}
            });
        }

        private final int count;
        private final List<Integer> expected;

        public GetQuantitiesTest(int count, List<Integer> expected) {
            this.count = count;
            this.expected = expected;
        }

        @Test
        public void getQuantities() {
            assertEquals(expected, QuestionUtils.getQuantities(count));
        }
    }
}
