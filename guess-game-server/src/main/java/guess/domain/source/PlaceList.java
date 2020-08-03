package guess.domain.source;

import java.util.List;

/**
 * Place list.
 */
public class PlaceList {
    private List<Place> places;

    public PlaceList() {
    }

    public PlaceList(List<Place> places) {
        this.places = places;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
