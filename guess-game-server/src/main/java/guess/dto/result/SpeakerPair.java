package guess.dto.result;

public class SpeakerPair {
    private final String name;
    private final String fileName;

    public SpeakerPair(String name, String fileName) {
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
