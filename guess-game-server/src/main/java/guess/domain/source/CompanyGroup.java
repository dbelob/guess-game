package guess.domain.source;

import java.util.List;
import java.util.Objects;

/**
 * Company group.
 */
public class CompanyGroup {
    private String name;
    private List<String> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyGroup)) return false;
        CompanyGroup that = (CompanyGroup) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "CompanyGroup{" +
                "name='" + name + '\'' +
                ", items=" + items +
                '}';
    }
}
