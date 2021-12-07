package guess.domain.source;

import guess.domain.Identifier;
import guess.domain.Language;
import guess.util.LocalizationUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Speaker.
 */
public class Speaker extends Identifier {
    public static class SpeakerPhoto {
        private final String photoFileName;
        private final ZonedDateTime photoUpdatedAt;

        public SpeakerPhoto(String photoFileName, ZonedDateTime photoUpdatedAt) {
            this.photoFileName = photoFileName;
            this.photoUpdatedAt = photoUpdatedAt;
        }
    }

    public static class SpeakerSocials {
        private final String twitter;
        private final String gitHub;
        private final String habr;

        public SpeakerSocials(String twitter, String gitHub, String habr) {
            this.twitter = twitter;
            this.gitHub = gitHub;
            this.habr = habr;
        }
    }

    public static class SpeakerDegrees {
        private final boolean javaChampion;
        private final boolean mvp;
        private final boolean mvpReconnect;

        public SpeakerDegrees(boolean javaChampion, boolean mvp, boolean mvpReconnect) {
            this.javaChampion = javaChampion;
            this.mvp = mvp;
            this.mvpReconnect = mvpReconnect;
        }
    }

    private String photoFileName;
    private ZonedDateTime photoUpdatedAt;
    private List<LocaleItem> name;
    private List<LocaleItem> bio;
    private String twitter;
    private String gitHub;
    private String habr;
    private boolean javaChampion;
    private boolean mvp;
    private boolean mvpReconnect;

    private List<Long> companyIds;
    private List<Company> companies = new ArrayList<>();

    public Speaker() {
        this.companyIds = new ArrayList<>();
    }

    public Speaker(long id, SpeakerPhoto photo, List<LocaleItem> name, List<Company> companies,
                   List<LocaleItem> bio, SpeakerSocials socials, SpeakerDegrees degrees) {
        super(id);

        this.photoFileName = photo.photoFileName;
        this.photoUpdatedAt = photo.photoUpdatedAt;
        this.name = name;
        this.bio = bio;
        this.twitter = socials.twitter;
        this.gitHub = socials.gitHub;
        this.habr = socials.habr;
        this.javaChampion = degrees.javaChampion;
        this.mvp = degrees.mvp;
        this.mvpReconnect = degrees.mvpReconnect;
        this.companies = companies;
        this.companyIds = companies.stream()
                .map(Company::getId)
                .toList();
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    public ZonedDateTime getPhotoUpdatedAt() {
        return photoUpdatedAt;
    }

    public void setPhotoUpdatedAt(ZonedDateTime photoUpdatedAt) {
        this.photoUpdatedAt = photoUpdatedAt;
    }

    public List<LocaleItem> getName() {
        return name;
    }

    public void setName(List<LocaleItem> name) {
        this.name = name;
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

    public String getHabr() {
        return habr;
    }

    public void setHabr(String habr) {
        this.habr = habr;
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

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public List<LocaleItem> getNameWithLastNameFirst() {
        if (name == null) {
            return null;
        }

        List<LocaleItem> result = new ArrayList<>();

        for (LocaleItem localeItem : name) {
            var language = Language.getLanguageByCode(localeItem.getLanguage());

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
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Speaker{" +
                "id=" + getId() +
                ", fileName='" + photoFileName + '\'' +
                ", name=" + name +
                '}';
    }
}
