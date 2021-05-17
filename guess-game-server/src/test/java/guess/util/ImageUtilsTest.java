package guess.util;

import guess.domain.source.image.ImageFormat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

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

        try (MockedStatic<ImageUtils> mockedStatic = Mockito.mockStatic(ImageUtils.class)) {
            mockedStatic.when(() -> ImageUtils.getImageByUrlString(Mockito.anyString()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> ImageUtils.getImageByUrl(Mockito.nullable(URL.class)))
                    .thenAnswer(
                            (Answer<BufferedImage>) invocation -> {
                                Object[] args = invocation.getArguments();
                                URL url = (URL) args[0];

                                if (url.equals(validUrlWithParameters)) {
                                    return expected;
                                } else {
                                    throw new IOException();
                                }
                            }
                    );

            assertEquals(expected, ImageUtils.getImageByUrlString(VALID_HTTP_URL_STRING));
            assertThrows(IOException.class, () -> ImageUtils.getImageByUrlString("https://invalid.com"));
        }
    }

    @Test
    void needUpdate() throws IOException {
        final String IMAGE_400X400_URL_STRING = "https://valid.com/image?w=400";
        final String IMAGE_1X1_URL_STRING = "https://valid.com/image?w=1";

        try (MockedStatic<ImageUtils> mockedStatic = Mockito.mockStatic(ImageUtils.class)) {
            mockedStatic.when(() -> ImageUtils.needUpdate(Mockito.nullable(String.class), Mockito.anyString()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> ImageUtils.getImageByUrl(Mockito.any(URL.class)))
                    .thenAnswer(
                            (Answer<BufferedImage>) invocation -> {
                                Object[] args = invocation.getArguments();
                                URL url = (URL) args[0];

                                return ImageIO.read(url);
                            }
                    );
            mockedStatic.when(() -> ImageUtils.getImageByUrlString(Mockito.nullable(String.class)))
                    .thenAnswer(
                            (Answer<BufferedImage>) invocation -> {
                                Object[] args = invocation.getArguments();
                                String urlString = (String) args[0];

                                switch (urlString) {
                                    case IMAGE_400X400_URL_STRING:
                                        return createImage(400, 400);
                                    case IMAGE_1X1_URL_STRING:
                                        return createImage(1, 1);
                                    default:
                                        throw new IOException();
                                }
                            }
                    );

            assertFalse(ImageUtils.needUpdate(null, JPG_IMAGE_400x400_PATH));
            assertFalse(ImageUtils.needUpdate(IMAGE_400X400_URL_STRING, JPG_IMAGE_400x400_PATH));
            assertFalse(ImageUtils.needUpdate(IMAGE_1X1_URL_STRING, JPG_IMAGE_400x400_PATH));

            assertTrue(ImageUtils.needUpdate(IMAGE_400X400_URL_STRING, JPG_IMAGE_1x1_PATH));
            assertFalse(ImageUtils.needUpdate(IMAGE_1X1_URL_STRING, JPG_IMAGE_1x1_PATH));
        }
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
        final String JPG_IMAGE_1X1_URL_STRING = "https://valid.com/fileName3.jpg";
        final String FILE_NAME0 = "fileName0.jpg";
        final String FILE_NAME1 = "fileName1.png";
        final String FILE_NAME2 = "fileName2.jpg";
        final String FILE_NAME3 = "fileName3.jpg";

        try (MockedStatic<ImageUtils> imageUtilsMockedStatic = Mockito.mockStatic(ImageUtils.class);
             MockedStatic<FileUtils> fileUtilsMockedStatic = Mockito.mockStatic(FileUtils.class);
             MockedStatic<ImageIO> imageIOMockedStatic = Mockito.mockStatic(ImageIO.class)) {
            imageUtilsMockedStatic.when(() -> ImageUtils.create(Mockito.anyString(), Mockito.anyString()))
                    .thenCallRealMethod();
            imageUtilsMockedStatic.when(() -> ImageUtils.getImageByUrlString(Mockito.nullable(String.class)))
                    .thenAnswer(
                            (Answer<BufferedImage>) invocation -> {
                                Object[] args = invocation.getArguments();
                                String urlString = (String) args[0];

                                switch (urlString) {
                                    case JPG_IMAGE_400X400_URL_STRING:
                                        return ImageIO.read(Paths.get(JPG_IMAGE_400x400_PATH).toUri().toURL());
                                    case PNG_IMAGE_400X400_URL_STRING:
                                        return ImageIO.read(Paths.get(PNG_IMAGE_400x400_PATH).toUri().toURL());
                                    case JPG_IMAGE_1X1_URL_STRING:
                                        return ImageIO.read(Paths.get(JPG_IMAGE_1x1_PATH).toUri().toURL());
                                    default:
                                        return null;
                                }
                            }
                    );
            imageUtilsMockedStatic.when(() -> ImageUtils.getImageFormatByUrlString(Mockito.nullable(String.class)))
                    .thenAnswer(
                            (Answer<ImageFormat>) invocation -> {
                                Object[] args = invocation.getArguments();
                                String sourceUrl = (String) args[0];

                                switch (sourceUrl) {
                                    case JPG_IMAGE_400X400_URL_STRING:
                                        return ImageFormat.JPG;
                                    case PNG_IMAGE_400X400_URL_STRING:
                                        return ImageFormat.PNG;
                                    default:
                                        return null;
                                }
                            }
                    );
            imageIOMockedStatic.when(() -> ImageIO.write(Mockito.nullable(RenderedImage.class), Mockito.nullable(String.class), Mockito.any(File.class)))
                    .thenAnswer(
                            (Answer<Boolean>) invocation -> {
                                Object[] args = invocation.getArguments();
                                File file = (File) args[2];

                                return !FILE_NAME2.equals(file.getName());
                            }
                    );

            assertDoesNotThrow(() -> ImageUtils.create(JPG_IMAGE_400X400_URL_STRING, FILE_NAME0));
            assertDoesNotThrow(() -> ImageUtils.create(PNG_IMAGE_400X400_URL_STRING, FILE_NAME1));
            assertThrows(IOException.class, () -> ImageUtils.create(JPG_IMAGE_400X400_URL_STRING, FILE_NAME2));
            assertThrows(IllegalStateException.class, () -> ImageUtils.create(JPG_IMAGE_1X1_URL_STRING, FILE_NAME3));
        }
    }
}
