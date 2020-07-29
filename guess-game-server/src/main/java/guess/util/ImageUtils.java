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

    private ImageUtils() {
    }

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

        BufferedImage image = getImageByUrl(sourceUrl);
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
     * Checks for need to update file.
     *
     * @param sourceUrl           source URL
     * @param destinationFileName destination file name
     * @return {@code true} if need to update, {@code false} otherwise
     * @throws IOException if read error occurs
     */
    public static boolean needUpdate(String sourceUrl, String destinationFileName) throws IOException {
        try {
            File file = new File(String.format("guess-game-web/src/assets/images/speakers/%s", destinationFileName));
            BufferedImage fileImage = ImageIO.read(file);

            if (fileImage.getWidth() < IMAGE_WIDTH) {
                BufferedImage urlImage = getImageByUrl(sourceUrl);

                return (fileImage.getWidth() < urlImage.getWidth());
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new IOException(String.format("Can't read image file %s for '%s' URL", destinationFileName, sourceUrl), e);
        }
    }

    /**
     * Gets image by URL.
     *
     * @param sourceUrl source URL
     * @return image
     * @throws IOException if read error occurs
     */
    private static BufferedImage getImageByUrl(String sourceUrl) throws IOException {
        String urlSpec = String.format("%s?w=%d", sourceUrl, IMAGE_WIDTH);
        URL url = new URL(urlSpec);
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            log.error("Can't get image by URL {}", urlSpec);
            throw e;
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
}
