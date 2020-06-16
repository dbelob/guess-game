package guess.domain.source;

import guess.domain.Language;
import guess.util.LocalizationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Speaker.
 */
public class Speaker {
    private long id;
    private String fileName;
    private List<LocaleItem> name;
    private List<LocaleItem> company;
    private List<LocaleItem> bio;
    private String twitter;
    private String gitHub;
    private boolean javaChampion;
    private boolean mvp;
    private boolean mvpReconnect;

    public Speaker() {
    }

    public Speaker(long id, String fileName, List<LocaleItem> name, List<LocaleItem> company,
                   List<LocaleItem> bio, String twitter, String gitHub, boolean javaChampion, boolean mvp,
                   boolean mvpReconnect) {
        this.id = id;
        this.fileName = fileName;
        this.name = name;
        this.company = company;
        this.bio = bio;
        this.twitter = twitter;
        this.gitHub = gitHub;
        this.javaChampion = javaChampion;
        this.mvp = mvp;
        this.mvpReconnect = mvpReconnect;
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

    public List<LocaleItem> getBio() {
        return bio;
    }

    public void setBio(List<LocaleItem> bio) {
        this.bio = bio;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getGitHub() {
        return gitHub;
    }

    public void setGitHub(String gitHub) {
        this.gitHub = gitHub;
    }

    public boolean isJavaChampion() {
        return javaChampion;
    }

    public void setJavaChampion(boolean javaChampion) {
        this.javaChampion = javaChampion;
    }

    public boolean isMvp() {
        return mvp;
    }

    public void setMvp(boolean mvp) {
        this.mvp = mvp;
    }

    public boolean isMvpReconnect() {
        return mvpReconnect;
    }

    public void setMvpReconnect(boolean mvpReconnect) {
        this.mvpReconnect = mvpReconnect;
    }

    public boolean isAnyMvp() {
        return (mvp || mvpReconnect);
    }

    public List<LocaleItem> getNameWithLastNameFirst() {
        if (name == null) {
            return name;
        }

        List<LocaleItem> result = new ArrayList<>();

        for (LocaleItem localeItem : name) {
            Language language = Language.getLanguageByCode(localeItem.getLanguage());

            if (language != null) {
                String localeName = LocalizationUtils.getString(name, language).trim();
                int lastIndex = localeName.lastIndexOf(' ');
                String resultLocaleName;

                if ((lastIndex >= 0) && ((lastIndex + 1) <= localeName.length())) {
                    resultLocaleName = localeName.substring(lastIndex + 1) + ' ' + localeName.substring(0, lastIndex);
                } else {
                    resultLocaleName = localeName;
                }

                result.add(new LocaleItem(localeItem.getLanguage(), resultLocaleName));
            } else {
                result.add(localeItem);
            }
        }

        return result;
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
