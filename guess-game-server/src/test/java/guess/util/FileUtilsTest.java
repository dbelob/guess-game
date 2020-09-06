package guess.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileUtils class tests")
class FileUtilsTest {
    static final String DIRECTORY_NAME0 = "directory0";
    static final String DIRECTORY_NAME1 = "directory1";

    @BeforeEach
    void setUp() throws IOException {
        FileUtils.deleteDirectory(DIRECTORY_NAME0);
        FileUtils.deleteDirectory(DIRECTORY_NAME1);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(DIRECTORY_NAME0);
        FileUtils.deleteDirectory(DIRECTORY_NAME1);
    }

    @Test
    void deleteDirectory() throws IOException {
        Path directoryPath = Path.of(DIRECTORY_NAME0);
        Path filePath = Path.of(DIRECTORY_NAME0 + "/file.ext");

        // Delete directory
        assertFalse(Files.exists(filePath));
        assertFalse(Files.exists(directoryPath));

        Files.createDirectory(directoryPath);
        Files.createFile(filePath);

        assertTrue(Files.exists(directoryPath) && Files.isDirectory(directoryPath));
        assertTrue(Files.exists(filePath) && !Files.isDirectory(filePath));

        FileUtils.deleteDirectory(DIRECTORY_NAME0);

        assertFalse(Files.exists(filePath));
        assertFalse(Files.exists(directoryPath));

        // Delete file
        Files.createFile(directoryPath);

        assertTrue(Files.exists(directoryPath) && !Files.isDirectory(directoryPath));

        FileUtils.deleteDirectory(DIRECTORY_NAME0);

        assertFalse(Files.exists(directoryPath));
    }

    @Test
    void checkAndCreateDirectory() throws IOException {
        File directory = new File(DIRECTORY_NAME1);
        File subDirectory = new File(DIRECTORY_NAME1 + "/subdirectory");

        assertFalse(directory.exists());

        // Create directory
        FileUtils.checkAndCreateDirectory(directory);

        assertTrue(directory.exists() && directory.isDirectory());

        // Try create directory again
        FileUtils.checkAndCreateDirectory(directory);

        assertTrue(directory.exists() && directory.isDirectory());

        FileUtils.deleteDirectory(DIRECTORY_NAME1);

        assertFalse(directory.exists());

        // Create file with same name
        assertTrue(directory.createNewFile());

        assertTrue(directory.exists() && !directory.isDirectory());

        // Try to create directory with name of existing file
        assertThrows(IOException.class, () -> FileUtils.checkAndCreateDirectory(directory));

        // Try to create directory with parent directory name of existing file
        assertThrows(IOException.class, () -> FileUtils.checkAndCreateDirectory(subDirectory));
    }
}
