package guess.domain.source.contentful;

public class Locale {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Locale{" +
                "code='" + code + '\'' +
                '}';
    }
}
