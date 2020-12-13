package guess.domain.source;

import java.util.List;
import java.util.Objects;

public class CompanySynonyms {
    private String name;
    private List<String> synonyms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanySynonyms)) return false;
        CompanySynonyms that = (CompanySynonyms) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "CompanySynonyms{" +
                "name='" + name + '\'' +
                ", synonyms=" + synonyms +
                '}';
    }
}
