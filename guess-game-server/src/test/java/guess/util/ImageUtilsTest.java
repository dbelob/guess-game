package guess.util;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ImageUtils.class)
public class ImageUtilsTest {
    private static final String VALID_IMAGE_PATH = "../guess-game-web/src/assets/images/speakers/0000.jpg";
    private static final String INVALID_IMAGE_PATH = "../guess-game-web/src/assets/images/speakers/invalid.jpg";
    private static URL validUrl;
    private static URL invalidUrl;

    @BeforeClass
    public static void init() throws MalformedURLException {
        validUrl = Paths.get(VALID_IMAGE_PATH).toUri().toURL();
        invalidUrl = Paths.get(INVALID_IMAGE_PATH).toUri().toURL();
    }

    @Test
    public void getImageByUrl() throws IOException {
        assertNotNull(ImageUtils.getImageByUrl(validUrl));
        assertThrows(IOException.class, () -> ImageUtils.getImageByUrl(invalidUrl));
    }

    @Test
    public void getImageByUrlString() throws IOException {
        String validHttpUrlString = "http://valid.com";
        String invalidHttpUrlString = "http://invalid.com";
        URL validUrlWithParameters = new URL(String.format("%s?w=%d", validHttpUrlString, ImageUtils.IMAGE_WIDTH));
        URL invalidUrlWithParameters = new URL(String.format("%s?w=%d", invalidHttpUrlString, ImageUtils.IMAGE_WIDTH));
        BufferedImage expected = new BufferedImage(
                1,
                1,
                BufferedImage.TYPE_INT_RGB);

        PowerMockito.mockStatic(ImageUtils.class);
        Mockito.when(ImageUtils.getImageByUrlString(Mockito.anyString())).thenCallRealMethod();
        Mockito.when(ImageUtils.getImageByUrl(validUrlWithParameters)).thenReturn(expected);
        Mockito.when(ImageUtils.getImageByUrl(invalidUrlWithParameters)).thenThrow(IOException.class);

        assertEquals(expected, ImageUtils.getImageByUrlString(validHttpUrlString));
        assertThrows(IOException.class, () -> ImageUtils.getImageByUrlString(invalidHttpUrlString));
    }
}
