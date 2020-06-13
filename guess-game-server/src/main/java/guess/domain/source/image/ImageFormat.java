package guess.domain.source.image;

import java.util.Arrays;
import java.util.List;

public enum ImageFormat {
    JPG(List.of("jpg", "jpeg", "00_jpg_srz")),
    PNG(List.of("png"));

    private List<String> fileExtensions;

    ImageFormat(List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    public static ImageFormat getImageFormatByExtension(String extension) {
        return Arrays.stream(ImageFormat.values())
                .filter(imageFormat -> imageFormat.getFileExtensions().stream()
                        .anyMatch(fileExtension -> fileExtension.equalsIgnoreCase(extension)))
                .findFirst()
                .orElse(null);
    }
}
