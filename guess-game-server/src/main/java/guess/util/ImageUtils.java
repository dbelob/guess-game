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

    static final String OUTPUT_DIRECTORY_NAME = "output";
    static final int IMAGE_WIDTH = 400;

    private ImageUtils() {
    }

    /**
     * Gets image by URL.
     *
     * @param url source URL
     * @return image
     * @throws IOException if read error occurs
     */
    static BufferedImage getImageByUrl(URL url) throws IOException {
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            log.error("Can't get image by URL {}", url);
            throw e;
        }
    }

    /**
     * Gets image by URL string.
     *
     * @param urlString source URL string
     * @return image
     * @throws IOException if read error occurs
     */
    static BufferedImage getImageByUrlString(String urlString) throws IOException {
        String urlSpec = String.format("%s?w=%d", urlString, IMAGE_WIDTH);
        URL url = new URL(urlSpec);

        return getImageByUrl(url);
    }

    /**
     * Checks for need to update file.
     *
     * @param targetPhotoUrl   source URL
     * @param resourceFileName resource file name
     * @return {@code true} if need to update, {@code false} otherwise
     * @throws IOException if read error occurs
     */
    public static boolean needUpdate(String targetPhotoUrl, String resourceFileName) throws IOException {
        BufferedImage fileImage = getImageByUrl(new File(resourceFileName).toURI().toURL());

        if (fileImage.getWidth() < IMAGE_WIDTH) {
            BufferedImage urlImage = getImageByUrlString(targetPhotoUrl);

            return (fileImage.getWidth() < urlImage.getWidth());
        } else {
            return false;
        }
    }

    /**
     * Gets image format from URL string.
     *
     * @param sourceUrl source URL string
     * @return image format
     */
    static ImageFormat getImageFormatByUrlString(String sourceUrl) {
        if ((sourceUrl == null) || sourceUrl.isEmpty()) {
            throw new IllegalArgumentException(String.format("Unknown image format, for URL: '%s'", sourceUrl));
        }

        int slashIndex = sourceUrl.lastIndexOf("/");
        String sourceUrlSuffix = (slashIndex >= 0) ? sourceUrl.substring(slashIndex + 1) : sourceUrl;
        int dotIndex = sourceUrlSuffix.lastIndexOf(".");

        if (dotIndex > 0) {
            String extension = sourceUrlSuffix.substring(dotIndex + 1);
            ImageFormat imageFormat = ImageFormat.getImageFormatByExtension(extension);

            return Objects.requireNonNull(imageFormat,
                    () -> String.format("Image format not found for '%s' extension", extension));
        } else {
            return ImageFormat.JPG;
        }
    }

    /**
     * Converts PNG image to JPG.
     *
     * @param image PNG image
     * @return JPG image
     */
    static BufferedImage convertPngToJpg(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        newImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

        return newImage;
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

        BufferedImage image = getImageByUrlString(sourceUrl);
        ImageFormat imageFormat = getImageFormatByUrlString(sourceUrl);

        if (!ImageFormat.JPG.equals(imageFormat)) {
            if (ImageFormat.PNG.equals(imageFormat)) {
                image = convertPngToJpg(image);
            } else {
                throw new IllegalStateException(String.format("Invalid image format %s for '%s' URL", imageFormat, sourceUrl));
            }
        }

        if (!ImageIO.write(image, "jpg", file)) {
            throw new IOException(String.format("Creation error for '%s' URL and '%s' file name", sourceUrl, destinationFileName));
        }
    }
}
