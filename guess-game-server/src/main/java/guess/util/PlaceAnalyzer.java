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

    private static String getFixedVenueAddress(String city, String venueAddress, List<FixingVenueAddress> fixingVenueAddresses) {
        for (FixingVenueAddress fixingVenueAddress : fixingVenueAddresses) {
            if (fixingVenueAddress.getCity().equals(city) &&
                    fixingVenueAddress.getInvalidVenueAddress().equals(venueAddress)) {
                return fixingVenueAddress.getValidVenueAddress();
            }
        }

        return venueAddress;
    }

    private static List<LocaleItem> fixVenueAddress(List<LocaleItem> city, List<LocaleItem> venueAddress) {
        List<FixingVenueAddress> enFixingVenueAddresses = List.of();
        List<FixingVenueAddress> ruFixingVenueAddresses = List.of(
                new FixingVenueAddress(
                        "Санкт-Петербург",
                        "пл. Победы, 1 , Гостиница «Park Inn by Radisson Пулковская»",
                        "пл. Победы, 1, Гостиница «Park Inn by Radisson Пулковская»"),
                new FixingVenueAddress(
                        "Москва",
                        "Международная ул., 16, Красногорск, Московская обл.,, МВЦ «Крокус Экспо»",
                        "Международная ул., 16, Красногорск, Московская обл., МВЦ «Крокус Экспо»")
        );

        String enVenueAddress = getFixedVenueAddress(
                LocalizationUtils.getString(city, Language.ENGLISH),
                LocalizationUtils.getString(venueAddress, Language.ENGLISH),
                enFixingVenueAddresses);
        String ruVenueAddress = getFixedVenueAddress(
                LocalizationUtils.getString(city, Language.RUSSIAN),
                LocalizationUtils.getString(venueAddress, Language.RUSSIAN),
                ruFixingVenueAddresses);

        return ContentfulUtils.extractLocaleItems(enVenueAddress, ruVenueAddress, true);
    }

    public static void main(String[] args) throws IOException, SpeakerDuplicatedException {
        SourceInformation resourceSourceInformation = YamlUtils.readSourceInformation();
        List<Event> events = resourceSourceInformation.getEvents();
        Map<CityVenueAddress, Place> placeMap = events.stream()
                .collect(Collectors.toMap(
                        e -> new CityVenueAddress(
                                LocalizationUtils.getString(e.getCity(), Language.RUSSIAN),
                                LocalizationUtils.getString(
                                        fixVenueAddress(e.getCity(), e.getVenueAddress()),
                                        Language.RUSSIAN)),
                        e -> new Place(
                                -1L,
                                e.getCity(),
                                fixVenueAddress(e.getCity(), e.getVenueAddress()),
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
