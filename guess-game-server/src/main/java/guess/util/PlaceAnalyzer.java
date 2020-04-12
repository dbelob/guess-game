package guess.util;

import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.LocaleItem;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class PlaceAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(PlaceAnalyzer.class);

    static class CityVenueAddress {
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

        public int getOrderNumber() {
            switch (city) {
                case "Saint Petersburg":
                case "Санкт-Петербург":
                    return 0;
                case "Moscow":
                case "Москва":
                    return 1;
                case "Novosibirsk":
                case "Новосибирск":
                    return 2;
                case "Helsinki":
                case "Хельсинки":
                    return 3;
                default:
                    return 4;
            }
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

    static class FixingVenueAddress {
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

    public static void main(String[] args) throws IOException, SpeakerDuplicatedException, NoSuchFieldException {
        SourceInformation resourceSourceInformation = YamlUtils.readSourceInformation();
        List<Event> events = resourceSourceInformation.getEvents();
        Map<CityVenueAddress, Place> placeMap = events.stream()
                .collect(Collectors.toMap(
                        e -> new CityVenueAddress(
                                LocalizationUtils.getString(e.getPlace().getCity(), Language.RUSSIAN),
                                LocalizationUtils.getString(
                                        ConferenceDataLoader.fixVenueAddress(e.getPlace()),
                                        Language.RUSSIAN)),
                        e -> new Place(
                                -1L,
                                e.getPlace().getCity(),
                                ConferenceDataLoader.fixVenueAddress(e.getPlace()),
                                e.getPlace().getMapCoordinates()),
                        (e1, e2) -> e1));
        List<Place> placesToAppend = placeMap.keySet().stream()
                .sorted(Comparator.comparing(CityVenueAddress::getOrderNumber)
                        .thenComparing(CityVenueAddress::getCity)
                        .thenComparing(CityVenueAddress::getVenueAddress))
                .map(placeMap::get)
                .collect(Collectors.toList());

        log.info("places: {}", placesToAppend.size());

        placesToAppend.forEach(
                p -> {
                    log.info("city: {}, venueAddress: {}",
                            LocalizationUtils.getString(p.getCity(), Language.RUSSIAN),
                            LocalizationUtils.getString(p.getVenueAddress(), Language.RUSSIAN));
                }
        );

        AtomicLong placesId = new AtomicLong(0);
        placesToAppend.forEach(p -> p.setId(placesId.getAndIncrement()));

        YamlUtils.dumpPlaces(placesToAppend, "places-to-append.yml");
    }
}
