package guess.util;

import guess.domain.source.image.ImageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Image utility methods.
 */
public class ImageUtils {
    private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);

    private static final String OUTPUT_DIRECTORY_NAME = "output";
    private static final int IMAGE_WIDTH = 400;

    /**
     * Creates image file from URL.
     *
     * @param sourceUrl           source URL
     * @param destinationFileName destination file name
     * @throws IOException if file creation error occurs
     */
    public static void create(String sourceUrl, String destinationFileName) throws IOException {
        File file = new File(String.format("%s/%s", OUTPUT_DIRECTORY_NAME, destinationFileName));
        FileUtils.checkAndCreateDirectory(file.getParentFile());

        URL url = new URL(String.format("%s?w=%d", sourceUrl, IMAGE_WIDTH));
        BufferedImage image = ImageIO.read(url);
        ImageFormat imageFormat = getImageFormatByUrl(sourceUrl);

        switch (imageFormat) {
            case JPG:
                // Nothing
                break;
            case PNG:
                image = convertPngToJpg(image);
                break;
            default:
                throw new IllegalStateException(String.format("Invalid image format %s for '%s' URL", imageFormat, sourceUrl));
        }

        if (!ImageIO.write(image, "jpg", file)) {
            throw new IOException(String.format("Creation error for '%s' URL and '%s' file name", sourceUrl, destinationFileName));
        }
    }

    /**
     * Gets image format from URL.
     *
     * @param sourceUrl source URL
     * @return image format
     */
    private static ImageFormat getImageFormatByUrl(String sourceUrl) {
        int index = sourceUrl.lastIndexOf(".");

        if (index > 0) {
            String extension = sourceUrl.substring(index + 1);
            ImageFormat imageFormat = ImageFormat.getImageFormatByExtension(extension);

            return Objects.requireNonNull(imageFormat,
                    () -> String.format("Image format not found for '%s' extension", extension));
        } else {
            throw new IllegalArgumentException(String.format("Unknown image format, for URL: '%s'", sourceUrl));
        }
    }

    /**
     * Converts PNG image to JPG.
     *
     * @param image PNG image
     * @return JPG image
     */
    private static BufferedImage convertPngToJpg(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        newImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

        return newImage;
    }

    /**
     * Checks for need to update file.
     *
     * @param fileName file name
     * @return {@code true} if need to update, {@code false} otherwise
     * @throws IOException if file read error occurs
     */
    public static boolean needUpdate(String fileName) throws IOException {
        try {
            File file = new File(String.format("guess-game-web/src/assets/images/speakers/%s", fileName));
            BufferedImage image = ImageIO.read(file);

            return image.getWidth() < IMAGE_WIDTH;
        } catch (IOException e) {
            throw new IOException(String.format("Can't read image file %s", fileName), e);
        }
    }
}
