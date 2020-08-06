package guess.domain.source;

import java.util.List;

public abstract class Nameable {
    private List<LocaleItem> name;
    private List<LocaleItem> shortDescription;
    private List<LocaleItem> longDescription;

    public Nameable() {
    }

    public Nameable(List<LocaleItem> name, List<LocaleItem> shortDescription, List<LocaleItem> longDescription) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }

    public List<LocaleItem> getName() {
        return name;
    }

    public void setName(List<LocaleItem> name) {
        this.name = name;
    }

    public List<LocaleItem> getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(List<LocaleItem> shortDescription) {
        this.shortDescription = shortDescription;
    }

    public List<LocaleItem> getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(List<LocaleItem> longDescription) {
        this.longDescription = longDescription;
    }
}
