package guess.dto.result;

public class SpeakerPairDto {
    private final String name;
    private final String fileName;

    public SpeakerPairDto(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }
}
