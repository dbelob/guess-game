package guess.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Picture utility methods.
 */
public class PictureUtils {
    private static final Logger log = LoggerFactory.getLogger(PictureUtils.class);

    private static final String OUTPUT_DIRECTORY_NAME = "output";
    private static final int PICTURE_WIDTH = 400;

    public static void create(String sourceUrl, String destinationFileName) throws IOException {
        //TODO: delete duplication
        File file = new File(String.format("%s/%s", OUTPUT_DIRECTORY_NAME, destinationFileName));
        File parentFile = file.getParentFile();

        if (parentFile.exists()) {
            if (!parentFile.isDirectory()) {
                throw new IOException(String.format("'%s' is not directory", parentFile.getAbsolutePath()));
            }
        } else {
            if (!parentFile.mkdirs()) {
                throw new IOException(String.format("Creation error for '%s' directory", parentFile.getAbsolutePath()));
            }
        }

        URL url = new URL(String.format("%s?w=%d", sourceUrl, PICTURE_WIDTH));
        BufferedImage image = ImageIO.read(url);
        ImageIO.write(image, "jpg", file);

        //TODO: implement
    }

    public static boolean needUpdate(String fileName) {
        //TODO: implement
        return true;
    }
}
