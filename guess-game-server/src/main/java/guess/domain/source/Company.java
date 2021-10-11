package guess.domain.source;

import guess.domain.Language;

import java.util.List;

/**
 * Company.
 */
public class Company extends Nameable {
    public Company() {
    }

    public Company(long id, List<LocaleItem> name) {
        super(id, name);
    }

    public Company(long id, String enName) {
        this(id, List.of(new LocaleItem(Language.ENGLISH.getCode(), enName)));
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + getId() +
                ", name=" + getName() +
                '}';
    }
}
