package guess.domain.source;

import java.util.List;

/**
 * Places.
 */
public class Places {
    private List<Place> places;

    public Places() {
    }

    public Places(List<Place> places) {
        this.places = places;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
