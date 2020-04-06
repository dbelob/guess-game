package guess.domain.source;

import java.util.List;
import java.util.Objects;

/**
 * Place.
 */
public class Place {
    private long id;
    private List<LocaleItem> city;
    private List<LocaleItem> venueAddress;
    private String mapCoordinates;

    public Place(long id, List<LocaleItem> city, List<LocaleItem> venueAddress, String mapCoordinates) {
        this.id = id;
        this.city = city;
        this.venueAddress = venueAddress;
        this.mapCoordinates = mapCoordinates;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<LocaleItem> getCity() {
        return city;
    }

    public void setCity(List<LocaleItem> city) {
        this.city = city;
    }

    public List<LocaleItem> getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(List<LocaleItem> venueAddress) {
        this.venueAddress = venueAddress;
    }

    public String getMapCoordinates() {
        return mapCoordinates;
    }

    public void setMapCoordinates(String mapCoordinates) {
        this.mapCoordinates = mapCoordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return id == place.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", city=" + city +
                ", venueAddress=" + venueAddress +
                ", mapCoordinates='" + mapCoordinates + '\'' +
                '}';
    }
}
