package acme.guess.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class CommonUtilsTest {
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

    private int count;
    private List<Integer> expected;

    public CommonUtilsTest(int count, List<Integer> expected) {
        this.count = count;
        this.expected = expected;
    }

    @Test
    public void getQuantities() {
        assertEquals(expected, CommonUtils.getQuantities(count));
    }
}
