package guess.util;

import guess.domain.source.image.ImageFormat;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ImageUtils class tests")
class ImageUtilsTest {
    private static final String JPG_IMAGE_400x400_PATH = createImagePath("400x400.jpg");
    private static final String PNG_IMAGE_400x400_PATH = createImagePath("400x400.png");
    private static final String JPG_IMAGE_1x1_PATH = createImagePath("1x1.jpg");
    private static final String INVALID_IMAGE_PATH = "invalid.jpg";

    private static String createImagePath(String resourceFileName) {
        ClassLoader classLoader = ImageUtilsTest.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(resourceFileName)).getFile());

        return file.getAbsolutePath();
    }

    private BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @BeforeAll
    static void setUp() throws IOException {
        FileUtils.deleteDirectory(ImageUtils.OUTPUT_DIRECTORY_NAME);
    }

    @AfterAll
    static void tearDown() throws IOException {
        FileUtils.deleteDirectory(ImageUtils.OUTPUT_DIRECTORY_NAME);
    }

    @Test
    void getImageByUrl() throws IOException {
        URL validUrl = Paths.get(JPG_IMAGE_400x400_PATH).toUri().toURL();
        URL invalidUrl = Paths.get(INVALID_IMAGE_PATH).toUri().toURL();

        assertNotNull(ImageUtils.getImageByUrl(validUrl));
        assertThrows(IOException.class, () -> ImageUtils.getImageByUrl(invalidUrl));
    }

    @Test
    void getImageByUrlString() throws IOException {
        final String VALID_HTTP_URL_STRING = "https://valid.com";
        final URL validUrlWithParameters = new URL(String.format("%s?w=%d", VALID_HTTP_URL_STRING, ImageUtils.IMAGE_WIDTH));
        BufferedImage expected = createImage(1, 1);

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

        assertEquals(expected, ImageUtils.getImageByUrlString(VALID_HTTP_URL_STRING));
        assertThrows(IOException.class, () -> ImageUtils.getImageByUrlString("https://invalid.com"));
    }

    @Test
    void needUpdate() throws IOException {
        final String IMAGE_400X400_URL_STRING = "https://valid.com/image?w=400";
        final String IMAGE_1X1_URL_STRING = "https://valid.com/image?w=1";

        new MockUp<ImageUtils>() {
            @Mock
            BufferedImage getImageByUrlString(String urlString) throws IOException {
                switch (urlString) {
                    case IMAGE_400X400_URL_STRING:
                        return createImage(400, 400);
                    case IMAGE_1X1_URL_STRING:
                        return createImage(1, 1);
                    default:
                        throw new IOException();
                }
            }
        };

        assertFalse(ImageUtils.needUpdate(null, JPG_IMAGE_400x400_PATH));
        assertFalse(ImageUtils.needUpdate(IMAGE_400X400_URL_STRING, JPG_IMAGE_400x400_PATH));
        assertFalse(ImageUtils.needUpdate(IMAGE_1X1_URL_STRING, JPG_IMAGE_400x400_PATH));

        assertTrue(ImageUtils.needUpdate(IMAGE_400X400_URL_STRING, JPG_IMAGE_1x1_PATH));
        assertFalse(ImageUtils.needUpdate(IMAGE_1X1_URL_STRING, JPG_IMAGE_1x1_PATH));
    }

    @Test
    void getImageFormatByUrlString() {
        assertThrows(IllegalArgumentException.class, () -> ImageUtils.getImageFormatByUrlString(null));
        assertThrows(IllegalArgumentException.class, () -> ImageUtils.getImageFormatByUrlString(""));
        assertEquals(ImageFormat.JPG, ImageUtils.getImageFormatByUrlString("fileName"));
        assertEquals(ImageFormat.JPG, ImageUtils.getImageFormatByUrlString("fileName.jpg"));
        assertEquals(ImageFormat.PNG, ImageUtils.getImageFormatByUrlString("fileName.png"));
        assertEquals(ImageFormat.JPG, ImageUtils.getImageFormatByUrlString("url/fileName"));
        assertEquals(ImageFormat.JPG, ImageUtils.getImageFormatByUrlString("url/fileName.jpg"));
        assertEquals(ImageFormat.PNG, ImageUtils.getImageFormatByUrlString("url/fileName.png"));
        assertEquals(ImageFormat.JPG, ImageUtils.getImageFormatByUrlString("fileName.unknown"));
    }

    @Test
    void convertPngToJpg() throws IOException {
        BufferedImage pngImage = ImageIO.read(Paths.get(PNG_IMAGE_400x400_PATH).toUri().toURL());
        BufferedImage image = ImageUtils.convertPngToJpg(pngImage);

        assertNotNull(image);
        assertEquals(BufferedImage.TYPE_INT_RGB, image.getType());
    }

    @Test
    void create() {
        final String JPG_IMAGE_400X400_URL_STRING = "https://valid.com/fileName0.jpg";
        final String PNG_IMAGE_400X400_URL_STRING = "https://valid.com/fileName1.png";

        new MockUp<ImageUtils>() {
            @Mock
            BufferedImage getImageByUrlString(String urlString) throws IOException {
                switch (urlString) {
                    case JPG_IMAGE_400X400_URL_STRING:
                        return ImageIO.read(Paths.get(JPG_IMAGE_400x400_PATH).toUri().toURL());
                    case PNG_IMAGE_400X400_URL_STRING:
                        return ImageIO.read(Paths.get(PNG_IMAGE_400x400_PATH).toUri().toURL());
                    default:
                        return null;
                }
            }
        };

        assertDoesNotThrow(() -> ImageUtils.create(JPG_IMAGE_400X400_URL_STRING, "fileName0.jpg"));
        assertDoesNotThrow(() -> ImageUtils.create(PNG_IMAGE_400X400_URL_STRING, "fileName1.jpg"));

        new MockUp<ImageIO>() {
            @Mock
            boolean write(RenderedImage im,
                          String formatName,
                          File output) throws IOException {
                return false;
            }
        };

        assertThrows(IOException.class, () -> ImageUtils.create(JPG_IMAGE_400X400_URL_STRING, "fileName2.jpg"));

        new MockUp<ImageUtils>() {
            @Mock
            BufferedImage getImageByUrlString(String urlString) throws IOException {
                switch (urlString) {
                    case JPG_IMAGE_400X400_URL_STRING:
                        return ImageIO.read(Paths.get(JPG_IMAGE_400x400_PATH).toUri().toURL());
                    case PNG_IMAGE_400X400_URL_STRING:
                        return ImageIO.read(Paths.get(PNG_IMAGE_400x400_PATH).toUri().toURL());
                    default:
                        return null;
                }
            }

            @Mock
            ImageFormat getImageFormatByUrlString(String sourceUrl) {
                return null;
            }
        };

        assertThrows(IllegalStateException.class, () -> ImageUtils.create(JPG_IMAGE_400X400_URL_STRING, "fileName3.jpg"));
    }
}
