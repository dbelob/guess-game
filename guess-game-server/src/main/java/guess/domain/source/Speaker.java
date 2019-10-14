package guess.domain.source;

import java.util.List;

/**
 * Speaker.
 */
public class Speaker {
    private long id;
    private String fileName;
    private List<LocaleItem> name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<LocaleItem> getName() {
        return name;
    }

    public void setName(List<LocaleItem> name) {
        this.name = name;
    }
}
