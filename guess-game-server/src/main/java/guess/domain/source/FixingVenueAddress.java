package guess.domain.source;

public class FixingVenueAddress {
    private final String city;
    private final String invalidVenueAddress;
    private final String validVenueAddress;

    public FixingVenueAddress(String city, String invalidVenueAddress, String validVenueAddress) {
        this.city = city;
        this.invalidVenueAddress = invalidVenueAddress;
        this.validVenueAddress = validVenueAddress;
    }

    public String getCity() {
        return city;
    }

    public String getInvalidVenueAddress() {
        return invalidVenueAddress;
    }

    public String getValidVenueAddress() {
        return validVenueAddress;
    }
}
