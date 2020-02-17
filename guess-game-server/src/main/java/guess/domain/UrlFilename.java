package guess.domain;

/**
 * URL, filename pair.
 */
public class UrlFilename {
    private String url;
    private String filename;

    public UrlFilename(String url, String filename) {
        this.url = url;
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }
}
