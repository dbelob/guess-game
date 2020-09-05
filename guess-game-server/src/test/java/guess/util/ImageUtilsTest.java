package guess.util;

import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ImageUtils class tests")
class ImageUtilsTest {
    private static final String VALID_IMAGE_PATH = "../guess-game-web/src/assets/images/speakers/0000.jpg";
    private static final String INVALID_IMAGE_PATH = "../guess-game-web/src/assets/images/speakers/invalid.jpg";
    private static URL validUrl;
    private static URL invalidUrl;

    @BeforeAll
    public static void init() throws MalformedURLException {
        validUrl = Paths.get(VALID_IMAGE_PATH).toUri().toURL();
        invalidUrl = Paths.get(INVALID_IMAGE_PATH).toUri().toURL();
    }

    @Test
    void getImageByUrl() throws IOException {
        assertNotNull(ImageUtils.getImageByUrl(validUrl));
        assertThrows(IOException.class, () -> ImageUtils.getImageByUrl(invalidUrl));
    }

    @Test
    void getImageByUrlString() throws IOException {
        String validHttpUrlString = "http://valid.com";
        String invalidHttpUrlString = "http://invalid.com";
        URL validUrlWithParameters = new URL(String.format("%s?w=%d", validHttpUrlString, ImageUtils.IMAGE_WIDTH));
        BufferedImage expected = new BufferedImage(
                1,
                1,
                BufferedImage.TYPE_INT_RGB);

        new MockUp<ImageUtils>() {
            @Mock
            BufferedImage getImageByUrl(URL url) throws IOException {
                if (url.equals(validUrlWithParameters)) {
                    return expected;
                } else {
                    throw new IOException();
                }
            }
        };

        assertEquals(expected, ImageUtils.getImageByUrlString(validHttpUrlString));
        assertThrows(IOException.class, () -> ImageUtils.getImageByUrlString(invalidHttpUrlString));
    }
}
