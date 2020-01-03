package guess.domain.answer;

public class AnswerPair {
    private final String name;
    private final String fileName;

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
