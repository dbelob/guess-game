package guess.domain.source;

import guess.domain.Language;

import java.util.List;

/**
 * Company.
 */
public class Company extends Nameable {
    private String siteLink;

    public Company() {
    }

    public Company(long id, List<LocaleItem> name, String siteLink) {
        super(id, name);

        this.siteLink = siteLink;
    }

    public Company(long id, List<LocaleItem> name) {
        this(id, name, null);
    }

    public Company(long id, String enName) {
        this(id, List.of(new LocaleItem(Language.ENGLISH.getCode(), enName)));
    }

    public String getSiteLink() {
        return siteLink;
    }

    public void setSiteLink(String siteLink) {
        this.siteLink = siteLink;
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
                ", name=" + getName() +
                ", siteLink=" + siteLink +
                '}';
    }
}
