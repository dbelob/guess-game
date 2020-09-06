package guess.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * File utility methods.
 */
public class FileUtils {
    private FileUtils() {
    }

    /**
     * Deletes directory.
     *
     * @param directoryName directory name
     * @throws IOException if file iteration occurs
     */
    public static void deleteDirectory(String directoryName) throws IOException {
        Path directoryPath = Path.of(directoryName);

        if (Files.exists(directoryPath)) {
            try (Stream<Path> pathStream = Files.walk(directoryPath)) {
                pathStream
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
    }

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
