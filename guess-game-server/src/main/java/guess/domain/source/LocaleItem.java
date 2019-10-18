package guess.domain.source;

/**
 * Locale item.
 */
public class LocaleItem {
    private String language;
    private String text;

    public LocaleItem() {
    }

    public LocaleItem(String language, String text) {
        this.language = language;
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "LocaleItem{" +
                "language='" + language + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
