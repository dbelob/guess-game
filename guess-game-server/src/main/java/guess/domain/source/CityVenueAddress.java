package guess.domain.source;

import java.util.Objects;

public class CityVenueAddress {
    private final String city;
    private final String venueAddress;

    public CityVenueAddress(String city, String venueAddress) {
        this.city = city;
        this.venueAddress = venueAddress;
    }

    public String getCity() {
        return city;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityVenueAddress that = (CityVenueAddress) o;
        return Objects.equals(city, that.city) &&
                Objects.equals(venueAddress, that.venueAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, venueAddress);
    }
}
