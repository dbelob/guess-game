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

    public static Language getLanguageByCode(String code) {
        for (Language language : values()) {
            if (language.getCode().equals(code)) {
                return language;
            }
        }

        return null;
    }
}
