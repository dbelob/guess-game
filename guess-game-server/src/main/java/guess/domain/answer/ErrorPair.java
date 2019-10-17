package guess.domain.answer;

public class ErrorPair {
    private String name;
    private String fileName;

    public ErrorPair(String name, String fileName) {
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
