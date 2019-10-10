package guess.domain;

public class AnswerPair {
    private String name;
    private String fileName;

    public AnswerPair(String name, String fileName) {
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
