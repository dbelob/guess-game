package guess.domain.source;

import java.util.List;

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
}
