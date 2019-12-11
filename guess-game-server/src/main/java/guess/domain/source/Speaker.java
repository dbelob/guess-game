package guess.domain.source;

import java.util.List;

/**
 * Speaker.
 */
public class Speaker {
    private long id;
    private String fileName;
    private List<LocaleItem> name;
    private List<LocaleItem> company;

    public Speaker() {
    }

    public Speaker(long id, String fileName, List<LocaleItem> name, List<LocaleItem> company) {
        this.id = id;
        this.fileName = fileName;
        this.name = name;
        this.company = company;
    }

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

    public List<LocaleItem> getCompany() {
        return company;
    }

    public void setCompany(List<LocaleItem> company) {
        this.company = company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Speaker speaker = (Speaker) o;

        return id == speaker.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Speaker{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", name=" + name +
                ", company=" + company +
                '}';
    }
}
