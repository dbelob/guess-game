package guess.domain.source.contentful.talk;

public class ContentfulTalkFields {
    private String name;
    private String nameEn;
    private Boolean sdTrack;
    private Boolean demoStage;

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

    public Boolean getSdTrack() {
        return sdTrack;
    }

    public void setSdTrack(Boolean sdTrack) {
        this.sdTrack = sdTrack;
    }

    public Boolean getDemoStage() {
        return demoStage;
    }

    public void setDemoStage(Boolean demoStage) {
        this.demoStage = demoStage;
    }
}
