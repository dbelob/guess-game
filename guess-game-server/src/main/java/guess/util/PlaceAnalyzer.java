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
import java.util.*;
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

    private static List<LocaleItem> fixVenueAddress(List<LocaleItem> city, List<LocaleItem> venueAddress) {
        final String SAINT_PETERSBURG_CITY = "Санкт-Петербург";
        final String FIXING_VENUE_ADDRESS = "пл. Победы, 1 , Гостиница «Park Inn by Radisson Пулковская»";
        final String FIXED_VENUE_ADDRESS = "пл. Победы, 1, Гостиница «Park Inn by Radisson Пулковская»";;

        if (SAINT_PETERSBURG_CITY.equals(LocalizationUtils.getString(city, Language.RUSSIAN)) &&
                FIXING_VENUE_ADDRESS.equals(LocalizationUtils.getString(venueAddress, Language.RUSSIAN))) {
            List<LocaleItem> localeItems = new ArrayList<>();
            String enText = LocalizationUtils.getString(venueAddress, Language.ENGLISH);

            if ((enText != null) && !enText.isEmpty()) {
                localeItems.add(new LocaleItem(
                        Language.ENGLISH.getCode(),
                        enText));
            }

            localeItems.add(new LocaleItem(
                    Language.RUSSIAN.getCode(),
                    FIXED_VENUE_ADDRESS));

            return localeItems;
        }

        return venueAddress;
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
