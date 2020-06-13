package guess.domain.source.contentful.speaker;

import guess.domain.source.contentful.ContentfulLink;

public class ContentfulSpeakerFields {
    private String name;
    private String nameEn;
    private String company;
    private String companyEn;
    private String bio;
    private String bioEn;
    private ContentfulLink photo;
    private String twitter;
    private String gitHub;
    private Boolean javaChampion;
    private Boolean mvp;
    private Boolean mvpReconnect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyEn() {
        return companyEn;
    }

    public void setCompanyEn(String companyEn) {
        this.companyEn = companyEn;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBioEn() {
        return bioEn;
    }

    public void setBioEn(String bioEn) {
        this.bioEn = bioEn;
    }

    public ContentfulLink getPhoto() {
        return photo;
    }

    public void setPhoto(ContentfulLink photo) {
        this.photo = photo;
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

    public Boolean getJavaChampion() {
        return javaChampion;
    }

    public void setJavaChampion(Boolean javaChampion) {
        this.javaChampion = javaChampion;
    }

    public Boolean getMvp() {
        return mvp;
    }

    public void setMvp(Boolean mvp) {
        this.mvp = mvp;
    }

    public Boolean getMvpReconnect() {
        return mvpReconnect;
    }

    public void setMvpReconnect(Boolean mvpReconnect) {
        this.mvpReconnect = mvpReconnect;
    }
}
