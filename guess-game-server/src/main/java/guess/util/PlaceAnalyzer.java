package guess.util;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.Place;
import guess.domain.source.SourceInformation;
import guess.util.yaml.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlaceAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(PlaceAnalyzer.class);

    static class CityVenueAddress {
        private String city;
        private String venueAddress;

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

    public static void main(String[] args) throws IOException, SpeakerDuplicatedException {
        SourceInformation resourceSourceInformation = YamlUtils.readSourceInformation();
        List<Event> events = resourceSourceInformation.getEvents();
        Map<CityVenueAddress, Place> placeMap = events.stream()
                .collect(Collectors.toMap(
                        e -> new CityVenueAddress(
                                LocalizationUtils.getString(e.getCity(), Language.RUSSIAN),
                                LocalizationUtils.getString(e.getVenueAddress(), Language.RUSSIAN)),
                        e -> new Place(
                                -1L,
                                e.getCity(),
                                e.getVenueAddress(),
                                e.getMapCoordinates()),
                        (e1, e2) -> e1));
        List<CityVenueAddress> cityVenueAddresses = placeMap.keySet().stream()
                .sorted(Comparator.comparing(CityVenueAddress::getCity).thenComparing(CityVenueAddress::getVenueAddress))
                .collect(Collectors.toList());

        log.info("places: {}", placeMap.size());

        cityVenueAddresses.forEach(
                e -> {
                    log.info("city: {}, venueAddress: {}", e.city, e.venueAddress);
                }
        );
    }
}
