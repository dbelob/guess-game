package guess.domain.source.contentful.speaker;

public class ContentfulSpeakerFields {
    private String name;
    private String nameEn;
    private String company;
    private String companyEn;
    private String bio;
    private String bioEn;
    private Boolean javaChampion;
    private Boolean sdSpeaker;

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

    public Boolean getJavaChampion() {
        return javaChampion;
    }

    public void setJavaChampion(Boolean javaChampion) {
        this.javaChampion = javaChampion;
    }

    public Boolean getSdSpeaker() {
        return sdSpeaker;
    }

    public void setSdSpeaker(Boolean sdSpeaker) {
        this.sdSpeaker = sdSpeaker;
    }
}
