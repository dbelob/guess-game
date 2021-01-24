package guess.domain.source;

import java.util.List;

public class Descriptionable extends Nameable {
    private List<LocaleItem> shortDescription;
    private List<LocaleItem> longDescription;

    public Descriptionable() {
    }

    public Descriptionable(long id, List<LocaleItem> name, List<LocaleItem> shortDescription, List<LocaleItem> longDescription) {
        super(id, name);

        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
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

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
