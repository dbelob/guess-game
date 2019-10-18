package guess.util;

import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.LocaleItem;
import guess.domain.source.Speaker;
import guess.domain.source.contentful.Locale;
import guess.domain.source.contentful.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contentful utility methods.
 */
public class ContentfulUtils {
    private enum ConferenceSpaceInfo {
        // Joker, JPoint, JBreak, TechTrain, C++, Hydra, SPTDC, DevOops, SmartData
        COMMON_SPACE_INFO("oxjq45e8ilak", "fdc0ca21c8c39ac5a33e1e20880cae6836ae837af73c2cfc822650483ee388fe", "fields.speaker"),
        // HolysJS
        HOLYS_JS_SPACE_INFO("nn534z2fqr9f", "1ca5b5d059930cd6681083617578e5a61187d1a71cbd75d4e0059cca3dc85f8c", "fields.speakers"),
        // DotNext
        DOT_NEXT_SPACE_INFO("9n3x4rtjlya6", "14e1427f8fbee9e5a089cd634fc60189c7aff2814b496fb0ad957b867a59503b", "fields.speaker"),
        // Heisenbug
        HEISENBUG_SPACE_INFO("ut4a3ciohj8i", "e7edd5951d844b80ef41166e30cb9645e4f89d11c8ac9eecdadb2a38c061b980", "fields.speaker"),
        // Mobius
        MOBIUS_SPACE_INFO("2grufn031spf", "d0c680ed11f68287348b6b8481d3313fde8c2d23cc8ce24a2b0ae254dd779e6d", "fields.speaker");

        private String spaceId;
        private String accessToken;
        private String speakerFieldName;

        ConferenceSpaceInfo(String spaceId, String accessToken, String speakerFieldName) {
            this.spaceId = spaceId;
            this.accessToken = accessToken;
            this.speakerFieldName = speakerFieldName;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ContentfulUtils.class);

    private static final String BASE_URL = "https://cdn.contentful.com/spaces/{spaceId}/{entityName}";
    private static final String MAIN_SPACE_ID = "2jxgmeypnru5";
    private static final String MAIN_ACCESS_TOKEN = "08f9e9e80ee347bd9f6017bf76f0a290c2ff0c28000946f7079f94a78974f090";

    private static String RUSSIAN_LOCALE = "ru-RU";
    private static Map<String, String> LOCALE_CODE_MAP = new HashMap<String, String>() {{
        put(RUSSIAN_LOCALE, LocalizationUtils.RUSSIAN_LANGUAGE);
    }};

    private static final RestTemplate restTemplate;

    static {
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>() {{
            add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
            add(new MappingJackson2HttpMessageConverter());
        }};
        restTemplate = new RestTemplate(converters);
    }

    /**
     * Gets locale codes.
     *
     * @param spaceId     space identifier
     * @param accessToken access token
     * @return locale codes
     */
    public static List<String> getLocales(String spaceId, String accessToken) {
        // https://cdn.contentful.com/spaces/{spaceId}/locales?access_token={accessToken}
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("access_token", accessToken);
        URI uri = builder
                .buildAndExpand(spaceId, "locales")
                .encode()
                .toUri();

        LocaleResponse response = restTemplate.getForObject(uri, LocaleResponse.class);

        return Objects.requireNonNull(response)
                .getItems().stream()
                .map(Locale::getCode)
                .collect(Collectors.toList());
    }

    /**
     * Gets event types.
     *
     * @param spaceId     space identifier
     * @param accessToken access token
     * @param locale      locale
     * @return event types
     */
    public static List<EventType> getEventTypes(String spaceId, String accessToken, String locale) {
        // https://cdn.contentful.com/spaces/{spaceId}/entries?access_token={accessToken}&locale={locale}&content_type=eventsList&select=fields.eventName
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("access_token", accessToken)
                .queryParam("locale", locale)
                .queryParam("content_type", "eventsList")
                .queryParam("select", "fields.eventName");
        URI uri = builder
                .buildAndExpand(spaceId, "entries")
                .encode()
                .toUri();

        ContentfulEventTypeResponse response = restTemplate.getForObject(uri, ContentfulEventTypeResponse.class);

        return Objects.requireNonNull(response)
                .getItems().stream()
                .map(et -> new EventType(
                        0L,
                        Collections.singletonList(new LocaleItem(transformLocale(locale), et.getFields().getEventName())),
                        null,
                        Collections.emptyList()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Gets events
     *
     * @param spaceId     space identifier
     * @param accessToken access token
     * @param locale      locale
     * @return events
     */
    public static List<Event> getEvents(String spaceId, String accessToken, String locale) {
        // https://cdn.contentful.com/spaces/{spaceId}/entries?access_token={accessToken}&locale={locale}&content_type=eventsCalendar&select=fields.conferenceName
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("access_token", accessToken)
                .queryParam("locale", locale)
                .queryParam("content_type", "eventsCalendar")
                .queryParam("select", "fields.conferenceName");
        URI uri = builder
                .buildAndExpand(spaceId, "entries")
                .encode()
                .toUri();

        ContentfulEventResponse response = restTemplate.getForObject(uri, ContentfulEventResponse.class);

        return Objects.requireNonNull(response)
                .getItems().stream()
                .map(e -> new Event(
                        null,
                        Collections.singletonList(new LocaleItem(transformLocale(locale), e.getFields().getConferenceName())),
                        null,
                        null,
                        null,
                        null,
                        new ArrayList<>()))
                .collect(Collectors.toList());
    }

    public static List<Speaker> getSpeakers(String spaceId, String accessToken, String locale, String speakerFieldName) {
        // https://cdn.contentful.com/spaces/{spaceId}/entries?access_token={accessToken}&locale={locale}&content_type=people&select=fields.name,fields.nameEn&{speakerFieldName}=trur
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("access_token", accessToken)
                .queryParam("locale", locale)
                .queryParam("content_type", "people")
                .queryParam("select", "fields.name,fields.nameEn")
                .queryParam(speakerFieldName, "true");  // only speakers
        URI uri = builder
                .buildAndExpand(spaceId, "entries")
                .encode()
                .toUri();

        ContentfulSpeakerResponse response = restTemplate.getForObject(uri, ContentfulSpeakerResponse.class);

        return Objects.requireNonNull(response)
                .getItems().stream()
                .map(s -> new Speaker(
                        0L,
                        null,
                        Arrays.asList(
                                new LocaleItem(
                                        LocalizationUtils.ENGLISH_LANGUAGE,
                                        s.getFields().getNameEn()),
                                new LocaleItem(
                                        LocalizationUtils.RUSSIAN_LANGUAGE,
                                        s.getFields().getName()))))
                .collect(Collectors.toList());
    }

    /**
     * Transforms locale code.
     *
     * @param locale source locale code
     * @return result locale code
     */
    private static String transformLocale(String locale) {
        return LOCALE_CODE_MAP.getOrDefault(locale, locale);
    }

    public static void main(String[] args) {
        List<String> locales = getLocales(MAIN_SPACE_ID, MAIN_ACCESS_TOKEN);
        log.info("Locales: {}", locales);

        for (String locale : locales) {
            List<EventType> eventTypes = getEventTypes(MAIN_SPACE_ID, MAIN_ACCESS_TOKEN, locale);
            log.info("Event types: {}", eventTypes);

            List<Event> events = getEvents(MAIN_SPACE_ID, MAIN_ACCESS_TOKEN, locale);
            log.info("Events: {}", events);
        }

        //TODO: combine event types, events (en, ru)

        for (ConferenceSpaceInfo conferenceSpaceInfo : ConferenceSpaceInfo.values()) {
            List<Speaker> speakers = getSpeakers(conferenceSpaceInfo.spaceId, conferenceSpaceInfo.accessToken, RUSSIAN_LOCALE, conferenceSpaceInfo.speakerFieldName);
            log.info("Speakers: {}", speakers);
        }

        // TODO: combine speakers, talk for all conferences
    }
}
