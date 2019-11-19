package guess.domain;

/**
 * Language.
 */
public enum Language {
    ENGLISH("en"),
    RUSSIAN("ru");

    private String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
