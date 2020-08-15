package guess.domain.source;

import guess.domain.Identifier;

import java.util.List;

/**
 * Place.
 */
public class Place extends Identifier {
    private List<LocaleItem> city;
    private List<LocaleItem> venueAddress;
    private String mapCoordinates;

    public Place() {
    }

    public Place(long id, List<LocaleItem> city, List<LocaleItem> venueAddress, String mapCoordinates) {
        super(id);

        this.city = city;
        this.venueAddress = venueAddress;
        this.mapCoordinates = mapCoordinates;
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
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + getId() +
                ", city=" + city +
                ", venueAddress=" + venueAddress +
                ", mapCoordinates='" + mapCoordinates + '\'' +
                '}';
    }
}
