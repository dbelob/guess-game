package guess.domain.source;

import guess.domain.Identifier;
import guess.domain.Language;

import java.util.List;

/**
 * Company.
 */
public class Company extends Identifier {
    private List<LocaleItem> name;

    public Company() {
    }

    public Company(long id, List<LocaleItem> name) {
        super(id);

        this.name = name;
    }

    public Company(long id, String enName) {
        this(id, List.of(new LocaleItem(Language.ENGLISH.getCode(), enName)));
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

    @Override
    public String toString() {
        return "Company{" +
                "id=" + getId() +
                "name=" + name +
                '}';
    }
}
