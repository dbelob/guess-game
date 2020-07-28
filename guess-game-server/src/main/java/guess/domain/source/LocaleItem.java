package guess.domain.source;

import java.io.Serializable;

/**
 * Locale item.
 */
public class LocaleItem implements Serializable {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocaleItem that = (LocaleItem) o;

        if (language != null ? !language.equals(that.language) : that.language != null) return false;
        return text != null ? text.equals(that.text) : that.text == null;
    }

    @Override
    public int hashCode() {
        int result = language != null ? language.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LocaleItem{" +
                "language='" + language + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
