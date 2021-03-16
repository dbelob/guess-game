package guess.dto.result;

/**
 * Speaker pair DTO.
 */
public class SpeakerPairDto {
    private final String name;
    private final String photoFileName;

    public SpeakerPairDto(String name, String photoFileName) {
        this.name = name;
        this.photoFileName = photoFileName;
    }

    public String getName() {
        return name;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }
}
