package guess.domain.source;

import guess.domain.Identifier;

import java.util.List;

public class Nameable extends Identifier {
    private List<LocaleItem> name;

    public Nameable() {
    }

    public Nameable(long id, List<LocaleItem> name) {
        super(id);

        this.name = name;
    }

    public List<LocaleItem> getName() {
        return name;
    }

    public void setName(List<LocaleItem> name) {
        this.name = name;
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
