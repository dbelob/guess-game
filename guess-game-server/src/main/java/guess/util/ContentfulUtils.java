package guess.util;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.contentful.ContentfulIncludes;
import guess.domain.source.contentful.ContentfulLink;
import guess.domain.source.contentful.ContentfulResponse;
import guess.domain.source.contentful.ContentfulSys;
import guess.domain.source.contentful.asset.ContentfulAsset;
import guess.domain.source.contentful.city.ContentfulCity;
import guess.domain.source.contentful.error.ContentfulErrorDetails;
import guess.domain.source.contentful.event.ContentfulEventResponse;
import guess.domain.source.contentful.eventtype.ContentfulEventTypeResponse;
import guess.domain.source.contentful.locale.ContentfulLocale;
import guess.domain.source.contentful.locale.ContentfulLocaleResponse;
import guess.domain.source.contentful.speaker.ContentfulSpeaker;
import guess.domain.source.contentful.speaker.ContentfulSpeakerResponse;
import guess.domain.source.contentful.talk.fields.ContentfulTalkFields;
import guess.domain.source.contentful.talk.response.*;
import guess.domain.source.extract.ExtractPair;
import guess.domain.source.extract.ExtractSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Contentful utility methods.
 */
public class ContentfulUtils {
    private static final Logger log = LoggerFactory.getLogger(ContentfulUtils.class);

    private static final String BASE_URL = "https://cdn.contentful.com/spaces/{spaceId}/{entityName}";
    private static final String MAIN_SPACE_ID = "2jxgmeypnru5";
    private static final String MAIN_ACCESS_TOKEN = "08f9e9e80ee347bd9f6017bf76f0a290c2ff0c28000946f7079f94a78974f090";

    private static final String FIELDS_SPEAKER_FIELD_NAME = "fields.speaker";
    private static final String FIELDS_CONFERENCES_FIELD_NAME = "fields.conferences";

    private static final String ACCESS_TOKEN_PARAM_NAME = "access_token";
    private static final String LOCALE_PARAM_NAME = "locale";
    private static final String CONTENT_TYPE_PARAM_NAME = "content_type";
    private static final String SELECT_PARAM_NAME = "select";
    private static final String ORDER_PARAM_NAME = "order";
    private static final String LIMIT_PARAM_NAME = "limit";

    private static final String ENTRIES_VARIABLE_VALUE = "entries";

    private static final int MAXIMUM_LIMIT = 1000;

    private static final String ENGLISH_LOCALE = "en";
    private static final String RUSSIAN_LOCALE = "ru-RU";

    private enum ConferenceSpaceInfo {
        // Joker, JPoint, JBreak, TechTrain, C++ Russia, Hydra, SPTDC, DevOops, SmartData
        COMMON_SPACE_INFO("oxjq45e8ilak", "fdc0ca21c8c39ac5a33e1e20880cae6836ae837af73c2cfc822650483ee388fe",
                FIELDS_SPEAKER_FIELD_NAME, FIELDS_CONFERENCES_FIELD_NAME, "fields.javaChampion",
                "fields.talksPresentation", ContentfulTalkResponseCommon.class),                        // fields.talksPresentation is list
        // HolyJS
        HOLY_JS_SPACE_INFO("nn534z2fqr9f", "1ca5b5d059930cd6681083617578e5a61187d1a71cbd75d4e0059cca3dc85f8c",
                "fields.speakers", "fields.conference", null,
                "fields.presentation", ContentfulTalkResponseHolyJs.class),                             // fields.presentation is single value
        // DotNext
        DOT_NEXT_SPACE_INFO("9n3x4rtjlya6", "14e1427f8fbee9e5a089cd634fc60189c7aff2814b496fb0ad957b867a59503b",
                FIELDS_SPEAKER_FIELD_NAME, "fields.conference", "fields.mvp,fields.mvpReconnect",
                "fields.talksPresentation,fields.presentation", ContentfulTalkResponseDotNext.class),   // fields.talksPresentation is list, fields.presentation is single value
        // Heisenbug
        HEISENBUG_SPACE_INFO("ut4a3ciohj8i", "e7edd5951d844b80ef41166e30cb9645e4f89d11c8ac9eecdadb2a38c061b980",
                FIELDS_SPEAKER_FIELD_NAME, FIELDS_CONFERENCES_FIELD_NAME, null,
                "fields.talksPresentation", ContentfulTalkResponseHeisenbug.class),                     // talksPresentation is single value
        // Mobius
        MOBIUS_SPACE_INFO("2grufn031spf", "d0c680ed11f68287348b6b8481d3313fde8c2d23cc8ce24a2b0ae254dd779e6d",
                FIELDS_SPEAKER_FIELD_NAME, FIELDS_CONFERENCES_FIELD_NAME, null,
                "fields.talkPresentation", ContentfulTalkResponseMobius.class);                         // talkPresentation is list

        private final String spaceId;
        private final String accessToken;

        private final String speakerFlagFieldName;
        private final String conferenceFieldName;

        private final String speakerAdditionalFieldNames;
        private final String talkAdditionalFieldNames;
        private final Class<? extends ContentfulTalkResponse<? extends ContentfulTalkFields>> talkResponseClass;

        ConferenceSpaceInfo(String spaceId, String accessToken, String speakerFlagFieldName, String conferenceFieldName,
                            String speakerAdditionalFieldNames, String talkAdditionalFieldNames,
                            Class<? extends ContentfulTalkResponse<? extends ContentfulTalkFields>> talkResponseClass) {
            this.spaceId = spaceId;
            this.accessToken = accessToken;
            this.speakerFlagFieldName = speakerFlagFieldName;
            this.conferenceFieldName = conferenceFieldName;
            this.speakerAdditionalFieldNames = speakerAdditionalFieldNames;
            this.talkAdditionalFieldNames = talkAdditionalFieldNames;
            this.talkResponseClass = talkResponseClass;
        }
    }

    private static final EnumMap<Conference, ConferenceSpaceInfo> CONFERENCE_SPACE_INFO_MAP = new EnumMap<>(Conference.class);
    private static final EnumMap<Conference, String> CONFERENCE_EVENT_TYPE_NAME_MAP = new EnumMap<>(Conference.class);
    private static final Map<String, Conference> EVENT_TYPE_NAME_CONFERENCE_MAP;

    private static final RestTemplate restTemplate;

    static {
        CONFERENCE_SPACE_INFO_MAP.put(Conference.JOKER, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.JPOINT, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.JBREAK, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.TECH_TRAIN, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.CPP_RUSSIA, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.HYDRA, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.SPTDC, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.DEV_OOPS, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.SMART_DATA, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.HOLY_JS, ConferenceSpaceInfo.HOLY_JS_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.DOT_NEXT, ConferenceSpaceInfo.DOT_NEXT_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.HEISENBUG, ConferenceSpaceInfo.HEISENBUG_SPACE_INFO);
        CONFERENCE_SPACE_INFO_MAP.put(Conference.MOBIUS, ConferenceSpaceInfo.MOBIUS_SPACE_INFO);

        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.JOKER, "Joker");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.JPOINT, "JPoint");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.JBREAK, "JBreak");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.TECH_TRAIN, "TechTrain");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.CPP_RUSSIA, "C++ Russia");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.HYDRA, "Hydra");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.SPTDC, "SPTDC");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.DEV_OOPS, "DevOops");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.SMART_DATA, "SmartData");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.HOLY_JS, "HolyJS");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.DOT_NEXT, "DotNext");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.HEISENBUG, "Heisenbug");
        CONFERENCE_EVENT_TYPE_NAME_MAP.put(Conference.MOBIUS, "Mobius");

        EVENT_TYPE_NAME_CONFERENCE_MAP = CONFERENCE_EVENT_TYPE_NAME_MAP.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(new MappingJackson2HttpMessageConverter());

        restTemplate = new RestTemplate(converters);
    }

    private ContentfulUtils() {
    }

    /**
     * Gets locale codes.
     *
     * @return locale codes
     */
    private static List<String> getLocales() {
        // https://cdn.contentful.com/spaces/{spaceId}/locales?access_token={accessToken}
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam(ACCESS_TOKEN_PARAM_NAME, MAIN_ACCESS_TOKEN);
        URI uri = builder
                .buildAndExpand(MAIN_SPACE_ID, "locales")
                .encode()
                .toUri();
        ContentfulLocaleResponse response = restTemplate.getForObject(uri, ContentfulLocaleResponse.class);

        return Objects.requireNonNull(response)
                .getItems().stream()
                .map(ContentfulLocale::getCode)
                .collect(Collectors.toList());
    }

    /**
     * Gets event types.
     *
     * @return event types
     */
    public static List<EventType> getEventTypes() {
        // https://cdn.contentful.com/spaces/{spaceId}/entries?access_token={accessToken}&locale={locale}&content_type=eventsList&select={fields}&order={fields}&limit=1000
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam(ACCESS_TOKEN_PARAM_NAME, MAIN_ACCESS_TOKEN)
                .queryParam(LOCALE_PARAM_NAME, "*")
                .queryParam(CONTENT_TYPE_PARAM_NAME, "eventsList")
                .queryParam(SELECT_PARAM_NAME, "fields.eventName,fields.eventDescriptions,fields.siteLink,fields.vkLink,fields.twLink,fields.fbLink,fields.youtubeLink,fields.telegramLink")
                .queryParam(ORDER_PARAM_NAME, "fields.eventName")
                .queryParam(LIMIT_PARAM_NAME, MAXIMUM_LIMIT);
        URI uri = builder
                .buildAndExpand(MAIN_SPACE_ID, ENTRIES_VARIABLE_VALUE)
                .encode()
                .toUri();
        ContentfulEventTypeResponse response = restTemplate.getForObject(uri, ContentfulEventTypeResponse.class);
        AtomicLong id = new AtomicLong(-1);

        return Objects.requireNonNull(response)
                .getItems().stream()
                .map(et -> {
                    Map<String, String> vkLink = et.getFields().getVkLink();
                    Map<String, String> twLink = et.getFields().getTwLink();
                    Map<String, String> fbLink = et.getFields().getFbLink();
                    Map<String, String> youtubeLink = et.getFields().getYoutubeLink();
                    Map<String, String> telegramLink = et.getFields().getTelegramLink();

                    return new EventType(
                            id.getAndDecrement(),
                            EVENT_TYPE_NAME_CONFERENCE_MAP.get(getFirstMapValue(et.getFields().getEventName()).trim()),
                            extractLocaleItems(
                                    extractString(et.getFields().getEventName().get(ENGLISH_LOCALE)),
                                    extractString(et.getFields().getEventName().get(RUSSIAN_LOCALE))),
                            null,
                            null,
                            extractLocaleItems(
                                    extractString(et.getFields().getEventDescriptions().get(ENGLISH_LOCALE)),
                                    extractString(et.getFields().getEventDescriptions().get(RUSSIAN_LOCALE))),
                            extractLocaleItems(
                                    extractString(et.getFields().getSiteLink().get(ENGLISH_LOCALE)),
                                    extractString(et.getFields().getSiteLink().get(RUSSIAN_LOCALE))),
                            (vkLink != null) ? getFirstMapValue(vkLink) : null,
                            (twLink != null) ? getFirstMapValue(twLink) : null,
                            (fbLink != null) ? getFirstMapValue(fbLink) : null,
                            (youtubeLink != null) ? getFirstMapValue(youtubeLink) : null,
                            (telegramLink != null) ? getFirstMapValue(telegramLink) : null,
                            Collections.emptyList(),
                            true);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets first map value.
     *
     * @param map map
     * @param <T> key type
     * @param <S> value type
     * @return first map value
     */
    static <T, S> S getFirstMapValue(Map<T, S> map) {
        Map.Entry<T, S> entry = map.entrySet().iterator().next();

        return entry.getValue();
    }

    /**
     * Gets events
     *
     * @param eventName event name
     * @param startDate start date
     * @return events
     */
    private static List<Event> getEvents(String eventName, LocalDate startDate) {
        // https://cdn.contentful.com/spaces/{spaceId}/entries?access_token={accessToken}&locale={locale}&content_type=eventsCalendar&select={fields}
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam(ACCESS_TOKEN_PARAM_NAME, MAIN_ACCESS_TOKEN)
                .queryParam(LOCALE_PARAM_NAME, "*")
                .queryParam(CONTENT_TYPE_PARAM_NAME, "eventsCalendar")
                .queryParam(SELECT_PARAM_NAME, "fields.conferenceName,fields.eventStart,fields.eventEnd,fields.conferenceLink,fields.eventCity,fields.youtubePlayList,fields.venueAddress,fields.addressLink")
                .queryParam(LIMIT_PARAM_NAME, MAXIMUM_LIMIT);

        if ((eventName != null) && !eventName.isEmpty()) {
            builder.queryParam("fields.eventPage.sys.contentType.sys.id", "eventsList");
            builder.queryParam("fields.eventPage.fields.eventName[match]", eventName);
        }

        if (startDate != null) {
            builder.queryParam("fields.eventStart[gte]", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(createUtcZonedDateTime(startDate)));
            builder.queryParam("fields.eventStart[lt]", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(createUtcZonedDateTime(startDate.plusDays(1))));
        }

        URI uri = builder
                .buildAndExpand(MAIN_SPACE_ID, ENTRIES_VARIABLE_VALUE)
                .encode()
                .toUri();
        ContentfulEventResponse response = restTemplate.getForObject(uri, ContentfulEventResponse.class);
        Map<String, ContentfulCity> cityMap = getCityMap(Objects.requireNonNull(response));
        Set<String> entryErrorSet = getEntryErrorSet(response);

        return response.getItems().stream()
                .map(e -> {
                    String nameEn = e.getFields().getConferenceName().get(ENGLISH_LOCALE);
                    String nameRu = e.getFields().getConferenceName().get(RUSSIAN_LOCALE);
                    if (nameEn == null) {
                        nameEn = nameRu;
                    }

                    LocalDate eventStartDate = createEventLocalDate(getFirstMapValue(e.getFields().getEventStart()));
                    LocalDate eventEndDate = (e.getFields().getEventEnd() != null) ?
                            createEventLocalDate(getFirstMapValue(e.getFields().getEventEnd())) :
                            eventStartDate;

                    ContentfulLink eventCityLink = getFirstMapValue(e.getFields().getEventCity());
                    Map<String, String> conferenceLink = e.getFields().getConferenceLink();
                    Map<String, String> venueAddress = e.getFields().getVenueAddress();
                    Map<String, String> youtubePlayList = e.getFields().getYoutubePlayList();
                    Map<String, String> addressLink = e.getFields().getAddressLink();

                    return new Event(
                            -1L,
                            null,
                            extractLocaleItems(
                                    extractEventName(nameEn, ENGLISH_LOCALE),
                                    extractEventName(nameRu, RUSSIAN_LOCALE)),
                            eventStartDate,
                            eventEndDate,
                            extractLocaleItems(
                                    extractLocaleValue(conferenceLink, ENGLISH_LOCALE),
                                    extractLocaleValue(conferenceLink, RUSSIAN_LOCALE)),
                            (youtubePlayList != null) ? getFirstMapValue(youtubePlayList) : null,
                            new Place(
                                    -1,
                                    extractLocaleItems(
                                            extractCity(eventCityLink, cityMap, entryErrorSet, ENGLISH_LOCALE, nameEn),
                                            extractCity(eventCityLink, cityMap, entryErrorSet, RUSSIAN_LOCALE, nameEn)),
                                    extractLocaleItems(
                                            extractLocaleValue(venueAddress, ENGLISH_LOCALE),
                                            extractLocaleValue(venueAddress, RUSSIAN_LOCALE)),
                                    (addressLink != null) ? getFirstMapValue(addressLink) : null),
                            Collections.emptyList());
                })
                .collect(Collectors.toList());
    }

    /**
     * Creates event zoned date time without offset (UTC).
     *
     * @param localDate date
     * @return zoned date time
     */
    static ZonedDateTime createUtcZonedDateTime(LocalDate localDate) {
        return ZonedDateTime.ofInstant(
                ZonedDateTime.of(
                        localDate,
                        LocalTime.of(0, 0, 0),
                        ZoneId.of(DateTimeUtils.EVENTS_ZONE_ID)).toInstant(),
                ZoneId.of("UTC"));
    }

    /**
     * Creates event local date from zoned date time string.
     *
     * @param zonedDateTimeString zoned date time string
     * @return event local date
     */
    static LocalDate createEventLocalDate(String zonedDateTimeString) {
        return ZonedDateTime.ofInstant(
                ZonedDateTime.parse(zonedDateTimeString).toInstant(),
                ZoneId.of(DateTimeUtils.EVENTS_ZONE_ID))
                .toLocalDate();
    }

    /**
     * Gets event.
     *
     * @param conference conference
     * @param startDate  start date
     * @return event
     */
    public static Event getEvent(Conference conference, LocalDate startDate) {
        Event fixedEvent = fixNonexistentEventError(conference, startDate);
        if (fixedEvent != null) {
            return fixedEvent;
        }

        String eventName = CONFERENCE_EVENT_TYPE_NAME_MAP.get(conference);
        List<Event> events = getEvents(eventName, startDate);

        if (events.isEmpty()) {
            throw new IllegalStateException(String.format("No events found for conference %s and start date %s (change conference and/or start date and rerun)",
                    conference, startDate));
        }

        if (events.size() > 1) {
            throw new IllegalStateException(String.format("Too much events found for conference %s and start date %s, events: %d (change conference and/or start date and rerun)",
                    conference, startDate, events.size()));
        }

        return events.get(0);
    }

    /**
     * Gets speakers.
     *
     * @param conferenceSpaceInfo conference space info
     * @param conferenceCode      conference code
     * @return speakers
     */
    private static List<Speaker> getSpeakers(ConferenceSpaceInfo conferenceSpaceInfo, String conferenceCode) {
        // https://cdn.contentful.com/spaces/{spaceId}/entries?access_token={accessToken}&content_type=people&select={fields}&{speakerFieldName}=true&limit=1000&fields.conferences={conferenceCode}
        StringBuilder selectingFields = new StringBuilder("sys.id,fields.name,fields.nameEn,fields.company,fields.companyEn,fields.bio,fields.bioEn,fields.photo,fields.twitter,fields.gitHub");
        String additionalFieldNames = conferenceSpaceInfo.speakerAdditionalFieldNames;

        if ((additionalFieldNames != null) && !additionalFieldNames.isEmpty()) {
            selectingFields.append(",").append(additionalFieldNames);
        }

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam(ACCESS_TOKEN_PARAM_NAME, conferenceSpaceInfo.accessToken)
                .queryParam(CONTENT_TYPE_PARAM_NAME, "people")
                .queryParam(SELECT_PARAM_NAME, selectingFields.toString())
                .queryParam(conferenceSpaceInfo.speakerFlagFieldName, "true")   // only speakers
                .queryParam(LIMIT_PARAM_NAME, MAXIMUM_LIMIT);

        if ((conferenceCode != null) && !conferenceCode.isEmpty()) {
            builder.queryParam(conferenceSpaceInfo.conferenceFieldName, conferenceCode);
        }

        URI uri = builder
                .buildAndExpand(conferenceSpaceInfo.spaceId, ENTRIES_VARIABLE_VALUE)
                .encode()
                .toUri();
        ContentfulSpeakerResponse response = restTemplate.getForObject(uri, ContentfulSpeakerResponse.class);
        AtomicLong id = new AtomicLong(-1);
        Map<String, ContentfulAsset> assetMap = getAssetMap(Objects.requireNonNull(response));
        Set<String> assetErrorSet = getAssetErrorSet(response);

        return Objects.requireNonNull(response)
                .getItems().stream()
                .map(s -> createSpeaker(s, assetMap, assetErrorSet, id))
                .collect(Collectors.toList());
    }

    /**
     * Gets speakers.
     *
     * @param conference     conference
     * @param conferenceCode conference code
     * @return speaker map
     */
    public static List<Speaker> getSpeakers(Conference conference, String conferenceCode) {
        ConferenceSpaceInfo conferenceSpaceInfo = CONFERENCE_SPACE_INFO_MAP.get(conference);

        return getSpeakers(conferenceSpaceInfo, conferenceCode);
    }

    /**
     * Gets talks.
     *
     * @param conferenceSpaceInfo conference space info
     * @param conferenceCode      conference code
     * @return talks
     */
    private static List<Talk> getTalks(ConferenceSpaceInfo conferenceSpaceInfo, String conferenceCode) {
        // https://cdn.contentful.com/spaces/{spaceId}/entries?access_token={accessToken}&content_type=talks&select={fields}&order={fields}&limit=1000&fields.conferences={conferenceCode}
        StringBuilder selectingFields = new StringBuilder("fields.name,fields.nameEn,fields.short,fields.shortEn,fields.long,fields.longEn,fields.speakers,fields.talkDay,fields.trackTime,fields.track,fields.language,fields.video,fields.sdTrack,fields.demoStage");
        String additionalFieldNames = conferenceSpaceInfo.talkAdditionalFieldNames;

        if ((additionalFieldNames != null) && !additionalFieldNames.isEmpty()) {
            selectingFields.append(",").append(additionalFieldNames);
        }

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam(ACCESS_TOKEN_PARAM_NAME, conferenceSpaceInfo.accessToken)
                .queryParam(CONTENT_TYPE_PARAM_NAME, "talks")
                .queryParam(SELECT_PARAM_NAME, selectingFields.toString())
                .queryParam(ORDER_PARAM_NAME, "fields.talkDay,fields.trackTime,fields.track")
                .queryParam(LIMIT_PARAM_NAME, MAXIMUM_LIMIT);

        if ((conferenceCode != null) && !conferenceCode.isEmpty()) {
            builder.queryParam(conferenceSpaceInfo.conferenceFieldName, conferenceCode);
        }

        URI uri = builder
                .buildAndExpand(conferenceSpaceInfo.spaceId, ENTRIES_VARIABLE_VALUE)
                .encode()
                .toUri();
        ContentfulTalkResponse<? extends ContentfulTalkFields> response = restTemplate.getForObject(uri, conferenceSpaceInfo.talkResponseClass);
        AtomicLong id = new AtomicLong(-1);
        Map<String, ContentfulAsset> assetMap = getAssetMap(Objects.requireNonNull(response));
        Set<String> entryErrorSet = getEntryErrorSet(response);
        Set<String> assetErrorSet = getAssetErrorSet(response);
        Map<String, Speaker> speakerMap = getSpeakerMap(response, assetMap, assetErrorSet);

        // Fix Contentful "notResolvable" error for one entry
        fixEntryNotResolvableError(conferenceSpaceInfo, entryErrorSet, speakerMap);

        return Objects.requireNonNull(response)
                .getItems().stream()
                .filter(t -> ((t.getFields().getSdTrack() == null) || !t.getFields().getSdTrack()) &&
                        ((t.getFields().getDemoStage() == null) || !t.getFields().getDemoStage()))   // not demo stage
                .map(t -> {
                    List<Speaker> speakers = t.getFields().getSpeakers().stream()
                            .filter(s -> {
                                String speakerId = s.getSys().getId();
                                boolean isErrorAsset = entryErrorSet.contains(speakerId);
                                if (isErrorAsset) {
                                    throw new IllegalArgumentException(String.format("Speaker id %s not resolvable for '%s' talk (change ContentfulUtils:fixEntryNotResolvableError() method and rerun)", speakerId, t.getFields().getNameEn()));
                                }

                                return true;
                            })
                            .map(s -> {
                                String speakerId = s.getSys().getId();
                                Speaker speaker = speakerMap.get(speakerId);
                                return Objects.requireNonNull(speaker,
                                        () -> String.format("Speaker id %s not found for '%s' talk", speakerId, t.getFields().getNameEn()));
                            })
                            .collect(Collectors.toList());

                    return new Talk(
                            id.getAndDecrement(),
                            extractLocaleItems(t.getFields().getNameEn(), t.getFields().getName()),
                            extractLocaleItems(t.getFields().getShortEn(), t.getFields().getShortRu()),
                            extractLocaleItems(t.getFields().getLongEn(), t.getFields().getLongRu()),
                            t.getFields().getTalkDay(),
                            t.getFields().getTrackTime(),
                            t.getFields().getTrack(),
                            extractLanguage(t.getFields().getLanguage()),
                            extractPresentationLinks(
                                    combineContentfulLinks(t.getFields().getPresentations(), t.getFields().getPresentation()),
                                    assetMap, assetErrorSet, t.getFields().getNameEn()),
                            extractVideoLinks(t.getFields().getVideo()),
                            speakers);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets talks
     *
     * @param conference     conference
     * @param conferenceCode conference code
     * @return talks
     */
    public static List<Talk> getTalks(Conference conference, String conferenceCode) {
        ConferenceSpaceInfo conferenceSpaceInfo = CONFERENCE_SPACE_INFO_MAP.get(conference);

        return getTalks(conferenceSpaceInfo, conferenceCode);
    }

    /**
     * Creates speaker from Contentful information.
     *
     * @param contentfulSpeaker    Contentful speaker
     * @param assetMap             map id/asset
     * @param assetErrorSet        set with error assets
     * @param id                   atomic identifier
     * @param checkEnTextExistence {@code true} if need to check English text existence, {@code false} otherwise
     * @return speaker
     */
    private static Speaker createSpeaker(ContentfulSpeaker contentfulSpeaker, Map<String, ContentfulAsset> assetMap,
                                         Set<String> assetErrorSet, AtomicLong id, boolean checkEnTextExistence) {
        return new Speaker(
                id.getAndDecrement(),
                extractPhoto(contentfulSpeaker.getFields().getPhoto(), assetMap, assetErrorSet, contentfulSpeaker.getFields().getNameEn()),
                extractLocaleItems(contentfulSpeaker.getFields().getNameEn(), contentfulSpeaker.getFields().getName(), checkEnTextExistence),
                extractLocaleItems(contentfulSpeaker.getFields().getCompanyEn(), contentfulSpeaker.getFields().getCompany(), checkEnTextExistence),
                extractLocaleItems(contentfulSpeaker.getFields().getBioEn(), contentfulSpeaker.getFields().getBio(), checkEnTextExistence),
                extractTwitter(contentfulSpeaker.getFields().getTwitter()),
                extractGitHub(contentfulSpeaker.getFields().getGitHub()),
                extractBoolean(contentfulSpeaker.getFields().getJavaChampion()),
                extractBoolean(contentfulSpeaker.getFields().getMvp()),
                extractBoolean(contentfulSpeaker.getFields().getMvpReconnect())
        );
    }

    /**
     * Creates speaker from Contentful information.
     *
     * @param contentfulSpeaker Contentful speaker
     * @param assetMap          map id/asset
     * @param assetErrorSet     set with error assets
     * @param id                atomic identifier
     * @return speaker
     */
    private static Speaker createSpeaker(ContentfulSpeaker contentfulSpeaker, Map<String, ContentfulAsset> assetMap,
                                         Set<String> assetErrorSet, AtomicLong id) {
        return createSpeaker(contentfulSpeaker, assetMap, assetErrorSet, id, true);
    }

    /**
     * Gets map id/speaker.
     *
     * @param response      response
     * @param assetMap      map id/asset
     * @param assetErrorSet set with error assets
     * @return map id/speaker
     */
    private static Map<String, Speaker> getSpeakerMap(ContentfulTalkResponse<? extends ContentfulTalkFields> response,
                                                      Map<String, ContentfulAsset> assetMap, Set<String> assetErrorSet) {
        AtomicLong id = new AtomicLong(-1);

        return (response.getIncludes() == null) ?
                Collections.emptyMap() :
                response.getIncludes().getEntry().stream()
                        .collect(Collectors.toMap(
                                s -> s.getSys().getId(),
                                s -> createSpeaker(s, assetMap, assetErrorSet, id, false)
                        ));
    }

    /**
     * Gets map id/asset.
     *
     * @param response response
     * @return map id/asset
     */
    private static Map<String, ContentfulAsset> getAssetMap(ContentfulResponse<?, ? extends ContentfulIncludes> response) {
        return (response.getIncludes() == null) ?
                Collections.emptyMap() :
                response.getIncludes().getAsset().stream()
                        .collect(Collectors.toMap(
                                a -> a.getSys().getId(),
                                a -> a
                        ));
    }

    /**
     * Gets map id/city.
     *
     * @param response response
     * @return map id/city
     */
    private static Map<String, ContentfulCity> getCityMap(ContentfulEventResponse response) {
        return (response.getIncludes() == null) ?
                Collections.emptyMap() :
                response.getIncludes().getEntry().stream()
                        .collect(Collectors.toMap(
                                a -> a.getSys().getId(),
                                a -> a
                        ));
    }

    /**
     * Gets error set.
     *
     * @param response response
     * @param linkType link type
     * @return error set
     */
    private static Set<String> getErrorSet(ContentfulResponse<?, ? extends ContentfulIncludes> response, String linkType) {
        return (response.getErrors() == null) ?
                Collections.emptySet() :
                response.getErrors().stream()
                        .filter(e -> {
                            ContentfulSys sys = e.getSys();
                            ContentfulErrorDetails details = e.getDetails();

                            return ((sys != null) && "notResolvable".equals(sys.getId()) && "error".equals(sys.getType()) &&
                                    (details != null) && "Link".equals(details.getType()) && linkType.equals(details.getLinkType()));
                        })
                        .map(e -> e.getDetails().getId())
                        .collect(Collectors.toSet());
    }

    /**
     * Gets entry error set.
     *
     * @param response response
     * @return error set
     */
    private static Set<String> getEntryErrorSet(ContentfulResponse<?, ? extends ContentfulIncludes> response) {
        return getErrorSet(response, "Entry");
    }

    /**
     * Gets asset error set.
     *
     * @param response response
     * @return error set
     */
    private static Set<String> getAssetErrorSet(ContentfulResponse<?, ? extends ContentfulIncludes> response) {
        return getErrorSet(response, "Asset");
    }

    /**
     * Extracts string, i.e. trims not null string.
     *
     * @param value source value
     * @return extracted string
     */
    static String extractString(String value) {
        return (value != null) ? value.trim() : null;
    }

    /**
     * Extracts boolean, considering null as false.
     *
     * @param value source value
     * @return extracted boolean
     */
    static boolean extractBoolean(Boolean value) {
        return (value != null) && value;
    }

    /**
     * Extracts value.
     *
     * @param value      source value
     * @param extractSet extract set
     * @return property
     */
    public static String extractProperty(String value, ExtractSet extractSet) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.isEmpty()) {
            return value;
        }

        for (ExtractPair extractPair : extractSet.getPairs()) {
            Pattern pattern = Pattern.compile(extractPair.getPatternRegex());
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) {
                return matcher.group(extractPair.getGroupIndex());
            }
        }

        throw new IllegalArgumentException(String.format(extractSet.getExceptionMessage(), value));
    }

    /**
     * Extracts Twitter username.
     *
     * @param value source value
     * @return extracted Twitter username
     */
    public static String extractTwitter(String value) {
        return extractProperty(value, new ExtractSet(
                List.of(new ExtractPair("^[\\s]*[@]?(\\w{1,15})[\\s]*$", 1)),
                "Invalid Twitter username: %s (change regular expression and rerun)"));
    }

    /**
     * Extracts GitHub username.
     *
     * @param value source value
     * @return extracted GitHub username
     */
    public static String extractGitHub(String value) {
        return extractProperty(value, new ExtractSet(
                List.of(
                        new ExtractPair("^[\\s]*((http(s)?://)?github.com/)?([a-zA-Z0-9\\-]+)(/)?[\\s]*$", 4),
                        new ExtractPair("^[\\s]*(http(s)?://)?([a-zA-Z0-9\\-]+).github.io/blog(/)?[\\s]*$", 3)),
                "Invalid GitHub username: %s (change regular expressions and rerun)"));
    }

    /**
     * Extracts talk language.
     *
     * @param language {@code true} if Russian, {@code false} otherwise
     * @return talk language
     */
    static String extractLanguage(Boolean language) {
        if (language != null) {
            return Boolean.TRUE.equals(language) ? Language.RUSSIAN.getCode() : Language.ENGLISH.getCode();
        } else {
            return null;
        }
    }

    /**
     * Combines Contentful links.
     *
     * @param presentations presentations
     * @param presentation  presentation
     * @return combined links
     */
    static List<ContentfulLink> combineContentfulLinks(List<ContentfulLink> presentations, ContentfulLink presentation) {
        List<ContentfulLink> contentfulLinks = new ArrayList<>();

        if (presentations != null) {
            contentfulLinks.addAll(presentations);
        }

        if (presentation != null) {
            contentfulLinks.add(presentation);
        }

        return contentfulLinks.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Extracts presentation links.
     *
     * @param links         links
     * @param assetMap      map id/asset
     * @param assetErrorSet set with error assets
     * @param talkNameEn    talk name
     * @return presentation link URLs
     */
    private static List<String> extractPresentationLinks(List<ContentfulLink> links, Map<String, ContentfulAsset> assetMap,
                                                         Set<String> assetErrorSet, String talkNameEn) {
        if (links == null) {
            return Collections.emptyList();
        }

        return links.stream()
                .filter(l -> {
                    String assetId = l.getSys().getId();
                    boolean isErrorAsset = assetErrorSet.contains(assetId);
                    if (isErrorAsset) {
                        log.warn("Asset (presentation link) id {} not resolvable for '{}' talk", assetId, talkNameEn);
                    }

                    return !isErrorAsset;
                })
                .map(l -> {
                    String assetId = l.getSys().getId();
                    ContentfulAsset asset = assetMap.get(assetId);
                    return extractAssetUrl(Objects.requireNonNull(asset,
                            () -> String.format("Asset (presentation link) id %s not found for '%s' talk", assetId, talkNameEn))
                            .getFields().getFile().getUrl());
                })
                .collect(Collectors.toList());
    }

    /**
     * Extracts video links.
     *
     * @param videoLink video link
     * @return video links
     */
    static List<String> extractVideoLinks(String videoLink) {
        List<String> videoLinks = new ArrayList<>();

        if (videoLink != null) {
            videoLinks.add(videoLink);
        }

        return videoLinks;
    }

    /**
     * Extracts photo.
     *
     * @param link          link
     * @param assetMap      map id/asset
     * @param assetErrorSet set with error assets
     * @param speakerNameEn speaker name
     * @return photo URL
     */
    private static String extractPhoto(ContentfulLink link, Map<String, ContentfulAsset> assetMap,
                                       Set<String> assetErrorSet, String speakerNameEn) {
        String assetId = link.getSys().getId();
        boolean isErrorAsset = assetErrorSet.contains(assetId);

        if (isErrorAsset) {
            log.warn("Asset (photo) id {} not resolvable for '{}' speaker", assetId, speakerNameEn);
            return null;
        }

        ContentfulAsset asset = assetMap.get(assetId);
        return extractAssetUrl(Objects.requireNonNull(asset,
                () -> String.format("Asset (photo) id %s not found for '%s' speaker", assetId, speakerNameEn))
                .getFields().getFile().getUrl());
    }

    /**
     * Extracts asset URL.
     *
     * @param value URL
     * @return URL with protocol
     */
    public static String extractAssetUrl(String value) {
        String property = extractProperty(value, new ExtractSet(
                List.of(new ExtractPair("^[\\s]*(http(s)?:)?//(.+)[\\s]*$", 3)),
                "Invalid asset URL: %s (change regular expression and rerun)"));

        if ((property == null) || property.isEmpty()) {
            return property;
        } else {
            return String.format("https://%s", property);
        }
    }

    /**
     * Extracts local items.
     *
     * @param enText               english text
     * @param ruText               russian text
     * @param checkEnTextExistence {@code true} if need to check English text existence, {@code false} otherwise
     * @return local items
     */
    static List<LocaleItem> extractLocaleItems(String enText, String ruText, boolean checkEnTextExistence) {
        enText = extractString(enText);
        ruText = extractString(ruText);

        if (checkEnTextExistence &&
                ((enText == null) || enText.isEmpty()) &&
                ((ruText != null) && !ruText.isEmpty())) {
            log.warn("Invalid arguments: enText is empty, ruText is not empty ('{}')", ruText);
        }

        if (Objects.equals(enText, ruText)) {
            ruText = null;
        }

        List<LocaleItem> localeItems = new ArrayList<>();

        if ((enText != null) && !enText.isEmpty()) {
            localeItems.add(new LocaleItem(
                    Language.ENGLISH.getCode(),
                    enText));
        }

        if ((ruText != null) && !ruText.isEmpty()) {
            localeItems.add(new LocaleItem(
                    Language.RUSSIAN.getCode(),
                    ruText));
        }

        return localeItems;
    }

    /**
     * Extracts local items.
     *
     * @param enText english text
     * @param ruText russian text
     * @return local items
     */
    static List<LocaleItem> extractLocaleItems(String enText, String ruText) {
        return extractLocaleItems(enText, ruText, true);
    }

    /**
     * Extracts event name.
     *
     * @param name   name
     * @param locale locale
     * @return event name
     */
    static String extractEventName(String name, String locale) {
        if (name == null) {
            return null;
        }

        switch (locale) {
            case ENGLISH_LOCALE:
                return name
                        .replaceAll("[\\s]*[.]*(Moscow){1}[\\s]*$", " Msc")
                        .replaceAll("[\\s]*[.]*(Piter){1}[\\s]*$", " SPb");
            case RUSSIAN_LOCALE:
                return name
                        .replaceAll("[\\s]*[.]*(Moscow){1}[\\s]*$", " Мск")
                        .replaceAll("[\\s]*[.]*(Piter){1}[\\s]*$", " СПб");
            default:
                throw new IllegalArgumentException(String.format("Unknown locale: %s (add new locale, change method and rerun)", locale));
        }
    }

    /**
     * Extracts value from map by locale.
     *
     * @param map    map
     * @param locale locale
     * @return attribute value
     */
    private static String extractLocaleValue(Map<String, String> map, String locale) {
        return (map != null) ? map.get(locale) : null;
    }

    /**
     * Extracts city name.
     *
     * @param link          link
     * @param cityMap       map id/city
     * @param entryErrorSet set with error entries
     * @param locale        locale
     * @param eventName     event name
     * @return city name
     */
    private static String extractCity(ContentfulLink link, Map<String, ContentfulCity> cityMap,
                                      Set<String> entryErrorSet, String locale, String eventName) {
        String entryId = link.getSys().getId();
        boolean isErrorAsset = entryErrorSet.contains(entryId);

        if (isErrorAsset) {
            log.warn("Entry (city name) id {} not resolvable for '{}' event", entryId, eventName);
            return null;
        }

        ContentfulCity city = cityMap.get(entryId);
        return Objects.requireNonNull(city,
                () -> String.format("Entry (city name) id %s not found for '%s' event", entryId, eventName))
                .getFields().getCityName().get(locale);
    }

    static Event fixNonexistentEventError(Conference conference, LocalDate startDate) {
        if (Conference.DOT_NEXT.equals(conference) && LocalDate.of(2016, 12, 7).equals(startDate)) {
            return new Event(
                    -1L,
                    null,
                    extractLocaleItems(
                            "DotNext 2016 Helsinki",
                            "DotNext 2016 Хельсинки"),
                    LocalDate.of(2016, 12, 7),
                    LocalDate.of(2016, 12, 7),
                    extractLocaleItems(
                            "https://dotnext-helsinki.com",
                            "https://dotnext-helsinki.com"),
                    "https://www.youtube.com/playlist?list=PLtWrKx3nUGBcaA5j9UT6XMnoGM6a2iCE5",
                    new Place(
                            15,
                            extractLocaleItems(
                                    "Helsinki",
                                    "Хельсинки"),
                            extractLocaleItems(
                                    "Microsoft Talo, Keilalahdentie 2-4, 02150 Espoo",
                                    null),
                            "60.1704769, 24.8279349"),
                    Collections.emptyList());
        } else {
            return null;
        }
    }

    /**
     * Fixes Contentful "notResolvable" error for one entry.
     *
     * @param conferenceSpaceInfo conference space info
     * @param entryErrorSet       entry error set
     * @param speakerMap          map id/speaker
     */
    private static void fixEntryNotResolvableError(ConferenceSpaceInfo conferenceSpaceInfo,
                                                   Set<String> entryErrorSet, Map<String, Speaker> speakerMap) {
        abstract class NotResolvableSpeaker {
            private final ConferenceSpaceInfo conferenceSpaceInfo;
            private final String entryId;

            public NotResolvableSpeaker(ConferenceSpaceInfo conferenceSpaceInfo, String entryId) {
                this.conferenceSpaceInfo = conferenceSpaceInfo;
                this.entryId = entryId;
            }

            public ConferenceSpaceInfo getConferenceSpaceInfo() {
                return conferenceSpaceInfo;
            }

            public String getEntryId() {
                return entryId;
            }

            public abstract Speaker createSpeaker(long id);
        }

        List<NotResolvableSpeaker> notResolvableSpeakers = List.of(
                new NotResolvableSpeaker(ConferenceSpaceInfo.COMMON_SPACE_INFO, "6yIC7EpG1EhejCEJDEsuqA") {
                    @Override
                    public Speaker createSpeaker(long id) {
                        return new Speaker(
                                id,
                                "https://images.ctfassets.net/oxjq45e8ilak/4K2YaPEYekHIGiGPFRPwyf/4b45c269f40874ef46370f2ef9824dcc/Chin.jpg",
                                extractLocaleItems(
                                        "Stephen Chin",
                                        null),
                                extractLocaleItems(
                                        "JFrog",
                                        null),
                                extractLocaleItems(
                                        "Stephen Chin is Senior Director of Developer Relations at JFrog, author of Raspberry Pi with Java, The Definitive Guide to Modern Client Development, and Pro JavaFX Platform. He has keynoted numerous Java conferences around the world including Oracle Code One (formerly JavaOne), where he is an 8-time Rock Star Award recipient. Stephen is an avid motorcyclist who has done evangelism tours in Europe, Japan, and Brazil, interviewing hackers in their natural habitat and posting the videos on <a href=\"http://nighthacking.com/\" target=\"_blank\">http://nighthacking.com/</a>. When he is not traveling, he enjoys teaching kids how to do embedded and robot programming together with his teenage daughter.",
                                        null),
                                "steveonjava",
                                "steveonjava",
                                true,
                                false,
                                false
                        );
                    }
                },
                new NotResolvableSpeaker(ConferenceSpaceInfo.COMMON_SPACE_INFO, "2i2OfmHelyMCiK2sCUoGsS") {
                    @Override
                    public Speaker createSpeaker(long id) {
                        return new Speaker(
                                id,
                                "https://images.ctfassets.net/oxjq45e8ilak/3msdNYfaMAagzUjHssfbws/6f2e74bfc57d4b263643854df894b11b/Egorov.jpg",
                                extractLocaleItems(
                                        "Sergey Egorov",
                                        "Сергей Егоров"),
                                extractLocaleItems(
                                        "Pivotal",
                                        null),
                                extractLocaleItems(
                                        "Sergei works at Pivotal on Project Reactor in Berlin, Germany.\n" +
                                                "\n" +
                                                "He is an active member of the open source community, member of the Apache Foundation, co-maintainer of the Testcontainers project, and a contributor to various OSS projects (Apache Groovy, Testcontainers, JBoss Modules, Spring Boot, to name a few), likes to share the knowledge and was presenting at different conferences and meetups in Russia, Germany, Ukraine, Norway, Denmark, Spain, and Estonia.\n" +
                                                "\n" +
                                                "He is passionate about DevOps topics, clouds, and infrastructure.\n" +
                                                "\n" +
                                                "Before Pivotal, he was working at Vivy, N26, Zalando, ZeroTurnaround, TransferWise, and other startups.\n",
                                        "Сергей работает в компании Pivotal в команде Project Reactor. Он является активным участником open source-сообщества, членом Apache Software Foundation, одним из главных разработчиков проекта Testcontainers и контрибьютором в разного рода проектах (Apache Groovy, Testcontainers, Spring Boot, JBoss Modules и не только)."),
                                "bsideup",
                                "bsideup",
                                true,
                                false,
                                false
                        );
                    }
                },
                new NotResolvableSpeaker(ConferenceSpaceInfo.COMMON_SPACE_INFO, "1FDbCMYfsEkiQG6s8CWQwS") {
                    @Override
                    public Speaker createSpeaker(long id) {
                        return new Speaker(
                                id,
                                "https://images.contentful.com/oxjq45e8ilak/4PO4u392HuG4KkkcyOEoEQ/454ad2e9abc50d5790dd20f6d71080d4/arun-feb25-2012.png",
                                extractLocaleItems(
                                        "Arun Gupta",
                                        null),
                                extractLocaleItems(
                                        "Couchbase",
                                        null),
                                extractLocaleItems(
                                        "Arun Gupta is the vice president of developer advocacy at Couchbase. He has been built and led developer communities for 10+ years at Sun, Oracle, and Red Hat. He has deep expertise in leading cross-functional teams to develop and execute strategy, planning, and execution of content, marketing campaigns, and programs. Prior to that he led engineering teams at Sun and is a founding member of the Java EE team. Gupta has authored more than 2,000 blog posts on technology. He has extensive speaking experience in more than 40 countries on myriad topics and is a JavaOne Rock Star for three years in a row. Gupta also founded the Devoxx4Kids chapter in the US and continues to promote technology education among children. An author of a best-selling book, an avid runner, a globe trotter, a Java Champion, a JUG leader, and a Docker Captain, he is easily accessible at @arungupta.",
                                        null),
                                "arungupta",
                                "arun-gupta",
                                true,
                                false,
                                false
                        );
                    }
                },
                new NotResolvableSpeaker(ConferenceSpaceInfo.COMMON_SPACE_INFO, "MPZSTxFNbbjBdf5M5uoOZ") {
                    @Override
                    public Speaker createSpeaker(long id) {
                        return new Speaker(
                                id,
                                "https://images.ctfassets.net/oxjq45e8ilak/24Bp61cBWjoYfrBtNvrabm/6f4cfb828f52f3e06d558559fac9c397/shaposhnik.jpg",
                                extractLocaleItems(
                                        "Roman Shaposhnik",
                                        "Роман Шапошник"),
                                extractLocaleItems(
                                        "ZEDEDA Inc.",
                                        null),
                                extractLocaleItems(
                                        "Roman is an open source software expert, currently serving on the board of directors for both The Apache Software Foundation and LF Edge. He has personally contributed to a variety of open source projects ranging from the Linux Kernel to Hadoop and ffmpeg. He is a co-founder and the vice president of product and strategy for Zededa, an edge virtualization startup. Throughout his career, Roman has held technical leadership roles at several well-known companies, including Sun Microsystems, Yahoo!, Cloudera and Pivotal Software. He holds a master's degree in mathematics and computer science from St. Petersburg State University. He likes German craft lagers and is fighting IPA invasion one seidla at a time.",
                                        null),
                                "rhatr",
                                null,
                                false,
                                false,
                                false
                        );
                    }
                },
                new NotResolvableSpeaker(ConferenceSpaceInfo.HOLY_JS_SPACE_INFO, "3YSoYRePW0OIeaAAkaweE6") {
                    @Override
                    public Speaker createSpeaker(long id) {
                        return new Speaker(
                                id,
                                "https://images.ctfassets.net/nn534z2fqr9f/32Ps6pruAEsOag6g88oSMa/c71710c584c7933020e4f96c2382427a/IMG_4618.JPG",
                                extractLocaleItems(
                                        "Irina Shestak",
                                        null),
                                extractLocaleItems(
                                        null,
                                        null),
                                extractLocaleItems(
                                        "tl;dr javascript, wombats and hot takes. Irina is a London via Vancouver software developer. She spends quite a bit of her time exploring the outdoors, gushing over trains, and reading some Beatniks.",
                                        null),
                                "_lrlna",
                                "lrlna",
                                false,
                                false,
                                false
                        );
                    }
                },
                new NotResolvableSpeaker(ConferenceSpaceInfo.HOLY_JS_SPACE_INFO, "2UddvLNyXmy4YaukAuE4Ao") {
                    @Override
                    public Speaker createSpeaker(long id) {
                        return new Speaker(
                                id,
                                "https://images.ctfassets.net/nn534z2fqr9f/5cXGxn3cYwwYQu0c6kWYKU/5438788ca0a8c4aa8c1b69a775fc9d7d/Kriger.jpg",
                                extractLocaleItems(
                                        "Sergei Kriger",
                                        "Сергей Кригер"),
                                extractLocaleItems(
                                        null,
                                        null),
                                extractLocaleItems(
                                        "Sergei fell in love with web development back in high school. He got a degree in Information Technologies at the University of Helsinki and has been spending his professional career working for web design studios in Helsinki and Munich. Sergei's focus areas are JavaScript development, UX and accessibility.",
                                        "Заболел веб-разработкой еще в школе (Windows 95, IE6, табличная верстка). Окончил Хельсинкский университет по специальности Information Technology, в настоящее время работает в Мюнхене фронтенд-разработчиком."),
                                "_sergeikriger",
                                null,
                                false,
                                false,
                                false
                        );
                    }
                }
        );

        long id = speakerMap.values().stream()
                .map(Speaker::getId)
                .min(Long::compare)
                .orElse(0L);

        for (NotResolvableSpeaker notResolvableSpeaker : notResolvableSpeakers) {
            String entryId = notResolvableSpeaker.getEntryId();

            if (notResolvableSpeaker.getConferenceSpaceInfo().equals(conferenceSpaceInfo) &&
                    !speakerMap.containsKey(entryId) && entryErrorSet.contains(entryId)) {
                speakerMap.put(entryId, notResolvableSpeaker.createSpeaker(--id));
                entryErrorSet.remove(entryId);
            }
        }
    }

    /**
     * Indicates the need to update event type.
     *
     * @param a first event type
     * @param b second event type
     * @return {@code true} if need to update, {@code false} otherwise
     */
    public static boolean needUpdate(EventType a, EventType b) {
        return !((a.getId() == b.getId()) &&
                (a.getConference() == b.getConference()) &&
                equals(a.getLogoFileName(), b.getLogoFileName()) &&
                equals(a.getName(), b.getName()) &&
                equals(a.getShortDescription(), b.getShortDescription()) &&
                equals(a.getLongDescription(), b.getLongDescription()) &&
                equals(a.getSiteLink(), b.getSiteLink()) &&
                equals(a.getVkLink(), b.getVkLink()) &&
                equals(a.getTwitterLink(), b.getTwitterLink()) &&
                equals(a.getFacebookLink(), b.getFacebookLink()) &&
                equals(a.getYoutubeLink(), b.getYoutubeLink()) &&
                equals(a.getTelegramLink(), b.getTelegramLink()));
    }

    /**
     * Indicates the need to update place.
     *
     * @param a first place
     * @param b second place
     * @return {@code true} if need to update, {@code false} otherwise
     */
    public static boolean needUpdate(Place a, Place b) {
        return !((a.getId() == b.getId()) &&
                equals(a.getCity(), b.getCity()) &&
                equals(a.getVenueAddress(), b.getVenueAddress()) &&
                equals(a.getMapCoordinates(), b.getMapCoordinates()));
    }

    /**
     * Indicates the need to update speaker.
     *
     * @param a first speaker
     * @param b second speaker
     * @return {@code true} if need to update, {@code false} otherwise
     */
    public static boolean needUpdate(Speaker a, Speaker b) {
        return !((a.getId() == b.getId()) &&
                equals(a.getPhotoFileName(), b.getPhotoFileName()) &&
                equals(a.getName(), b.getName()) &&
                equals(a.getCompany(), b.getCompany()) &&
                equals(a.getBio(), b.getBio()) &&
                equals(a.getTwitter(), b.getTwitter()) &&
                equals(a.getGitHub(), b.getGitHub()) &&
                (a.isJavaChampion() == b.isJavaChampion()) &&
                (a.isMvp() == b.isMvp()) &&
                (a.isMvpReconnect() == b.isMvpReconnect()));
    }

    /**
     * Indicates the need to update talk.
     *
     * @param a first talk
     * @param b second talk
     * @return {@code true} if need to update, {@code false} otherwise
     */
    public static boolean needUpdate(Talk a, Talk b) {
        return !((a.getId() == b.getId()) &&
                equals(a.getName(), b.getName()) &&
                equals(a.getShortDescription(), b.getShortDescription()) &&
                equals(a.getLongDescription(), b.getLongDescription()) &&
                equals(a.getTalkDay(), b.getTalkDay()) &&
                equals(a.getTrackTime(), b.getTrackTime()) &&
                equals(a.getTrack(), b.getTrack()) &&
                equals(a.getLanguage(), b.getLanguage()) &&
                equals(a.getPresentationLinks(), b.getPresentationLinks()) &&
                equals(a.getVideoLinks(), b.getVideoLinks()) &&
                equals(a.getSpeakerIds(), b.getSpeakerIds()));
    }

    /**
     * Indicates the need to update event.
     *
     * @param a first event
     * @param b second event
     * @return {@code true} if need to update, {@code false} otherwise
     */
    public static boolean needUpdate(Event a, Event b) {
        return !((a.getEventTypeId() == b.getEventTypeId()) &&
                equals(a.getName(), b.getName()) &&
                a.getStartDate().equals(b.getStartDate()) &&
                a.getEndDate().equals(b.getEndDate()) &&
                equals(a.getSiteLink(), b.getSiteLink()) &&
                equals(a.getYoutubeLink(), b.getYoutubeLink()) &&
                (a.getPlaceId() == b.getPlaceId()) &&
                equals(a.getTalkIds(), b.getTalkIds()));
    }

    private static <T> boolean equals(T a, T b) {
        return Objects.equals(a, b);
    }

    static <T> boolean equals(List<T> a, List<T> b) {
        if (a != null) {
            if (b != null) {
                return (a.containsAll(b) && b.containsAll(a));
            } else {
                return false;
            }
        } else {
            return (b == null);
        }
    }

    public static void main(String[] args) {
        List<String> locales = getLocales();
        log.info("Locales: {}, {}", locales.size(), locales);

        List<EventType> eventTypes = getEventTypes();
        log.info("Event types: {}, {}", eventTypes.size(), eventTypes);

        List<Event> events = getEvents(null, null);
        log.info("Events: {}, {}", events.size(), events);

        for (ConferenceSpaceInfo conferenceSpaceInfo : ConferenceSpaceInfo.values()) {
            log.info("Conference space info: {}", conferenceSpaceInfo);

            List<Speaker> speakers = getSpeakers(conferenceSpaceInfo, null);
            log.info("Speakers: {}, {}", speakers.size(), speakers);

            List<Talk> talks = getTalks(conferenceSpaceInfo, null);
            log.info("Talks: {}, {}", talks.size(), talks);
        }
    }
}
