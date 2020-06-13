package guess.util;

import java.io.File;
import java.io.IOException;

/**
 * File utility methods.
 */
public class FileUtils {
    /**
     * Checks existence and creates directory.
     *
     * @param file file
     * @throws IOException if file error occurs
     */
    public static void checkAndCreateDirectory(File file) throws IOException {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IOException(String.format("'%s' is not directory", file.getAbsolutePath()));
            }
        } else {
            if (!file.mkdirs()) {
                throw new IOException(String.format("Creation error for '%s' directory", file.getAbsolutePath()));
            }
        }
    }
}
