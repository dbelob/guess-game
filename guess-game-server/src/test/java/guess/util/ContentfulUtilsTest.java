package guess.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class ContentfulUtilsTest {
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null, null},
                {"", ""},
                {" ", ""},
                {"arungupta", "arungupta"},
                {" arungupta", "arungupta"},
                {"arungupta ", "arungupta"},
                {" arungupta ", "arungupta"},
                {"tagir_valeev", "tagir_valeev"},
                {"kuksenk0", "kuksenk0"},
                {"DaschnerS", "DaschnerS"},
                {"@dougqh", "dougqh"}
        });
    }

    private String value;
    private String expected;

    public ContentfulUtilsTest(String value, String expected) {
        this.value = value;
        this.expected = expected;
    }

    @Test
    public void extractTwitter() {
        assertEquals(expected, ContentfulUtils.extractTwitter(value));
    }
}
