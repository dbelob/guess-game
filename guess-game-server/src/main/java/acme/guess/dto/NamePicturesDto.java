package acme.guess.dto;

/**
 * Name, pictures DTO.
 */
public class NamePicturesDto {
    private String questionSetName;
    private int currentNumber;
    private int totalNumber;

    private String name;

    private long id0;
    private long id1;
    private long id2;
    private long id3;
    private String fileName0;
    private String fileName1;
    private String fileName2;
    private String fileName3;

    public NamePicturesDto(String questionSetName, int currentNumber, int totalNumber, String name, long id0, long id1, long id2, long id3, String fileName0, String fileName1, String fileName2, String fileName3) {
        this.questionSetName = questionSetName;
        this.currentNumber = currentNumber;
        this.totalNumber = totalNumber;
        this.name = name;
        this.id0 = id0;
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.fileName0 = fileName0;
        this.fileName1 = fileName1;
        this.fileName2 = fileName2;
        this.fileName3 = fileName3;
    }

    public String getQuestionSetName() {
        return questionSetName;
    }

    public int getCurrentNumber() {
        return currentNumber;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public String getName() {
        return name;
    }

    public long getId0() {
        return id0;
    }

    public long getId1() {
        return id1;
    }

    public long getId2() {
        return id2;
    }

    public long getId3() {
        return id3;
    }

    public String getFileName0() {
        return fileName0;
    }

    public String getFileName1() {
        return fileName1;
    }

    public String getFileName2() {
        return fileName2;
    }

    public String getFileName3() {
        return fileName3;
    }
}
