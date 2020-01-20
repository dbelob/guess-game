package guess.util;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.contentful.ContentfulIncludes;
import guess.domain.source.contentful.ContentfulLink;
import guess.domain.source.contentful.ContentfulResponse;
import guess.domain.source.contentful.ContentfulSys;
import guess.domain.source.contentful.asset.ContentfulAsset;
import guess.domain.source.contentful.error.ContentfulErrorDetails;
import guess.domain.source.contentful.event.ContentfulEventResponse;
import guess.domain.source.contentful.eventtype.ContentfulEventTypeResponse;
import guess.domain.source.contentful.locale.ContentfulLocale;
import guess.domain.source.contentful.locale.ContentfulLocaleResponse;
import guess.domain.source.contentful.speaker.ContentfulSpeaker;
import guess.domain.source.contentful.speaker.ContentfulSpeakerResponse;
import guess.domain.source.contentful.talk.fields.ContentfulTalkFields;
import guess.domain.source.contentful.talk.response.*;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Contentful utility methods.
 */
public class ContentfulUtils {
    private enum ConferenceSpaceInfo {
        // Joker, JPoint, JBreak, TechTrain, C++ Russia, Hydra, SPTDC, DevOops, SmartData
        COMMON_SPACE_INFO("oxjq45e8ilak", "fdc0ca21c8c39ac5a33e1e20880cae6836ae837af73c2cfc822650483ee388fe",
                "fields.speaker", "fields.conferences", "fields.javaChampion",
                "fields.talksPresentation", ContentfulTalkResponseCommon.class),                        // fields.talksPresentation is list
        // HolyJS
        HOLY_JS_SPACE_INFO("nn534z2fqr9f", "1ca5b5d059930cd6681083617578e5a61187d1a71cbd75d4e0059cca3dc85f8c",
                "fields.speakers", "fields.conference", null,
                "fields.presentation", ContentfulTalkResponseHolyJs.class),                             // fields.presentation is single value
        // DotNext
        DOT_NEXT_SPACE_INFO("9n3x4rtjlya6", "14e1427f8fbee9e5a089cd634fc60189c7aff2814b496fb0ad957b867a59503b",
                "fields.speaker", "fields.conference", "fields.mvp",
                "fields.talksPresentation,fields.presentation", ContentfulTalkResponseDotNext.class),   // fields.talksPresentation is list, fields.presentation is single value
        // Heisenbug
        HEISENBUG_SPACE_INFO("ut4a3ciohj8i", "e7edd5951d844b80ef41166e30cb9645e4f89d11c8ac9eecdadb2a38c061b980",
                "fields.speaker", "fields.conferences", null,
                "fields.talksPresentation", ContentfulTalkResponseHeisenbug.class),                     // talksPresentation is single value
        // Mobius
        MOBIUS_SPACE_INFO("2grufn031spf", "d0c680ed11f68287348b6b8481d3313fde8c2d23cc8ce24a2b0ae254dd779e6d",
                "fields.speaker", "fields.conferences", null,
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

    private static final Logger log = LoggerFactory.getLogger(ContentfulUtils.class);

    private static final String BASE_URL = "https://cdn.contentful.com/spaces/{spaceId}/{entityName}";
    private static final String MAIN_SPACE_ID = "2jxgmeypnru5";
    private static final String MAIN_ACCESS_TOKEN = "08f9e9e80ee347bd9f6017bf76f0a290c2ff0c28000946f7079f94a78974f090";
    private static final int MAXIMUM_LIMIT = 1000;

    private static Map<String, String> LOCALE_CODE_MAP = new HashMap<>() {{
        put("ru-RU", Language.RUSSIAN.getCode());
    }};

    private static Map<Conference, ConferenceSpaceInfo> CONFERENCE_SPACE_INFO_MAP = new HashMap<>() {{
        put(Conference.JOKER, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        put(Conference.JPOINT, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        put(Conference.JBREAK, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        put(Conference.TECH_TRAIN, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        put(Conference.CPP_RUSSIA, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        put(Conference.HYDRA, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        put(Conference.SPTDC, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        put(Conference.DEV_OOPS, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        put(Conference.SMART_DATA, ConferenceSpaceInfo.COMMON_SPACE_INFO);
        put(Conference.HOLY_JS, ConferenceSpaceInfo.HOLY_JS_SPACE_INFO);
        put(Conference.DOT_NEXT, ConferenceSpaceInfo.DOT_NEXT_SPACE_INFO);
        put(Conference.HEISENBUG, ConferenceSpaceInfo.HEISENBUG_SPACE_INFO);
        put(Conference.MOBIUS, ConferenceSpaceInfo.MOBIUS_SPACE_INFO);
    }};

    private static final RestTemplate restTemplate;

    static {
        List<HttpMessageConverter<?>> converters = new ArrayList<>() {{
            add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
            add(new MappingJackson2HttpMessageConverter());
        }};
        restTemplate = new RestTemplate(converters);
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
                .queryParam("access_token", MAIN_ACCESS_TOKEN);
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
     * @param locale locale
     * @return event types
     */
    private static List<EventType> getEventTypes(String locale) {
        // https://cdn.contentful.com/spaces/{spaceId}/entries?access_token={accessToken}&locale={locale}&content_type=eventsList&select={fields}
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("access_token", MAIN_ACCESS_TOKEN)
                .queryParam("locale", locale)
                .queryParam("content_type", "eventsList")
                .queryParam("select", "fields.eventName")
                .queryParam("limit", MAXIMUM_LIMIT);
        URI uri = builder
                .buildAndExpand(MAIN_SPACE_ID, "entries")
                .encode()
                .toUri();
        ContentfulEventTypeResponse response = restTemplate.getForObject(uri, ContentfulEventTypeResponse.class);

        return Objects.requireNonNull(response)
                .getItems().stream()
                .map(et -> new EventType(
                        0L,
                        Collections.singletonList(new LocaleItem(
                                transformLocale(locale),
                                extractString(et.getFields().getEventName()))),
                        null,
                        Collections.emptyList()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Gets events
     *
     * @param locale locale
     * @return events
     */
    private static List<Event> getEvents(String locale) {
        // https://cdn.contentful.com/spaces/{spaceId}/entries?access_token={accessToken}&locale={locale}&content_type=eventsCalendar&select={fields}
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("access_token", MAIN_ACCESS_TOKEN)
                .queryParam("locale", locale)
                .queryParam("content_type", "eventsCalendar")
                .queryParam("select", "fields.conferenceName")
                .queryParam("limit", MAXIMUM_LIMIT);
        URI uri = builder
                .buildAndExpand(MAIN_SPACE_ID, "entries")
                .encode()
                .toUri();
        ContentfulEventResponse response = restTemplate.getForObject(uri, ContentfulEventResponse.class);

        return Objects.requireNonNull(response)
                .getItems().stream()
                .map(e -> new Event(
                        null,
                        Collections.singletonList(new LocaleItem(
                                transformLocale(locale),
                                extractString(e.getFields().getConferenceName()))),
                        null,
                        null,
                        null,
                        null,
                        new ArrayList<>()))
                .collect(Collectors.toList());
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
                .queryParam("access_token", conferenceSpaceInfo.accessToken)
                .queryParam("content_type", "people")
                .queryParam("select", selectingFields.toString())
                .queryParam(conferenceSpaceInfo.speakerFlagFieldName, "true")   // only speakers
                .queryParam("limit", MAXIMUM_LIMIT);

        if ((conferenceCode != null) && !conferenceCode.isEmpty()) {
            builder.queryParam(conferenceSpaceInfo.conferenceFieldName, conferenceCode);
        }

        URI uri = builder
                .buildAndExpand(conferenceSpaceInfo.spaceId, "entries")
                .encode()
                .toUri();
        ContentfulSpeakerResponse response = restTemplate.getForObject(uri, ContentfulSpeakerResponse.class);
        AtomicLong id = new AtomicLong();
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
        StringBuilder selectingFields = new StringBuilder("fields.name,fields.nameEn,fields.short,fields.shortEn,fields.long,fields.longEn,fields.speakers,fields.video,fields.sdTrack,fields.demoStage");
        String additionalFieldNames = conferenceSpaceInfo.talkAdditionalFieldNames;

        if ((additionalFieldNames != null) && !additionalFieldNames.isEmpty()) {
            selectingFields.append(",").append(additionalFieldNames);
        }

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("access_token", conferenceSpaceInfo.accessToken)
                .queryParam("content_type", "talks")
                .queryParam("select", selectingFields.toString())
                .queryParam("order", "fields.talkDay,fields.trackTime,fields.track")
                .queryParam("limit", MAXIMUM_LIMIT);

        if ((conferenceCode != null) && !conferenceCode.isEmpty()) {
            builder.queryParam(conferenceSpaceInfo.conferenceFieldName, conferenceCode);
        }

        URI uri = builder
                .buildAndExpand(conferenceSpaceInfo.spaceId, "entries")
                .encode()
                .toUri();
        ContentfulTalkResponse<? extends ContentfulTalkFields> response = restTemplate.getForObject(uri, conferenceSpaceInfo.talkResponseClass);
        AtomicLong id = new AtomicLong();
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
                                    log.warn("Speaker id {} not resolvable for '{}' talk", speakerId, t.getFields().getNameEn());
                                }

                                return !isErrorAsset;
                            })
                            .map(s -> {
                                String speakerId = s.getSys().getId();
                                Speaker speaker = speakerMap.get(speakerId);
                                return Objects.requireNonNull(speaker,
                                        () -> String.format("Speaker id %s not found for '%s' talk", speakerId, t.getFields().getNameEn()));
                            })
                            .collect(Collectors.toList());

                    return new Talk(
                            id.getAndIncrement(),
                            extractLocaleItems(t.getFields().getNameEn(), t.getFields().getName()),
                            extractLocaleItems(t.getFields().getShortEn(), t.getFields().getShortRu()),
                            extractLocaleItems(t.getFields().getLongEn(), t.getFields().getLongRu()),
                            extractPresentationLinks(
                                    combineContentfulLinks(t.getFields().getPresentations(), t.getFields().getPresentation()),
                                    assetMap, assetErrorSet, t.getFields().getNameEn()),
                            extractString(t.getFields().getVideo()),
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
     * Transforms locale code.
     *
     * @param locale source locale code
     * @return result locale code
     */
    private static String transformLocale(String locale) {
        return LOCALE_CODE_MAP.getOrDefault(locale, locale);
    }

    /**
     * Gets name, speaker map.
     *
     * @return name, speaker map
     */
    //TODO: delete
    public static Map<String, Speaker> getNameSpeakerMap() {
        Map<String, Speaker> result = new HashMap<>();

        for (ConferenceSpaceInfo conferenceSpaceInfo : ConferenceSpaceInfo.values()) {
            List<Speaker> speakers = getSpeakers(conferenceSpaceInfo, null);

            for (Speaker speaker : speakers) {
                String englishName = LocalizationUtils.getString(speaker.getName(), Language.ENGLISH);
                String russianName = LocalizationUtils.getString(speaker.getName(), Language.RUSSIAN);

                if ((englishName != null) && !englishName.isEmpty() &&
                        (russianName != null) && !russianName.isEmpty()) {
                    result.put(englishName.trim(), speaker);
                }
            }
        }

        return result;
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
        return new Speaker(
                id.getAndIncrement(),
                extractPhoto(contentfulSpeaker.getFields().getPhoto(), assetMap, assetErrorSet, contentfulSpeaker.getFields().getNameEn()),
                extractLocaleItems(contentfulSpeaker.getFields().getNameEn(), contentfulSpeaker.getFields().getName()),
                extractLocaleItems(contentfulSpeaker.getFields().getCompanyEn(), contentfulSpeaker.getFields().getCompany()),
                extractLocaleItems(contentfulSpeaker.getFields().getBioEn(), contentfulSpeaker.getFields().getBio()),
                extractTwitter(contentfulSpeaker.getFields().getTwitter()),
                extractGitHub(contentfulSpeaker.getFields().getGitHub()),
                extractBoolean(contentfulSpeaker.getFields().getJavaChampion()),
                extractBoolean(contentfulSpeaker.getFields().getMvp())
        );
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
        AtomicLong id = new AtomicLong();

        return (response.getIncludes() == null) ?
                Collections.emptyMap() :
                response.getIncludes().getEntry().stream()
                        .collect(Collectors.toMap(
                                s -> s.getSys().getId(),
                                s -> createSpeaker(s, assetMap, assetErrorSet, id)
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
    private static String extractString(String value) {
        return (value != null) ? value.trim() : null;
    }

    /**
     * Extracts boolean, considering null as false.
     *
     * @param value source value
     * @return extracted boolean
     */
    private static boolean extractBoolean(Boolean value) {
        return (value != null) ? value : false;
    }

    /**
     * Extracts Twitter username.
     *
     * @param value source value
     * @return extracted Twitter username
     */
    public static String extractTwitter(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.isEmpty()) {
            return value;
        }

        Pattern pattern = Pattern.compile("^[\\s]*[@]?(\\w{1,15})[\\s]*$");
        Matcher matcher = pattern.matcher(value);

        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException(String.format("Invalid Twitter username: %s", value));
        }
    }

    /**
     * Extracts GitHub username.
     *
     * @param value source value
     * @return extracted GitHub username
     */
    public static String extractGitHub(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.isEmpty()) {
            return value;
        }

        Pattern pattern = Pattern.compile("^[\\s]*((http(s)?://)?github.com/)?([a-zA-Z0-9\\-]+)(/)?[\\s]*$");
        Matcher matcher = pattern.matcher(value);

        if (matcher.matches()) {
            return matcher.group(4);
        } else {
            pattern = Pattern.compile("^[\\s]*(http(s)?://)?([a-zA-Z0-9\\-]+).github.io/blog(/)?[\\s]*$");
            matcher = pattern.matcher(value);

            if (matcher.matches()) {
                return matcher.group(3);
            } else {
                throw new IllegalArgumentException(String.format("Invalid GitHub username: %s", value));
            }
        }
    }

    /**
     * Combines Contentful links.
     *
     * @param presentations presentations
     * @param presentation  presentation
     * @return combined links
     */
    private static List<ContentfulLink> combineContentfulLinks(List<ContentfulLink> presentations, ContentfulLink presentation) {
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
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.isEmpty()) {
            return value;
        }

        Pattern pattern = Pattern.compile("^[\\s]*(http(s)?:)?//(.+)[\\s]*$");
        Matcher matcher = pattern.matcher(value);

        if (matcher.matches()) {
            return String.format("https://%s", matcher.group(3));
        } else {
            throw new IllegalArgumentException(String.format("Invalid asset URL: %s", value));
        }
    }

    /**
     * Extracts local items.
     *
     * @param enText english text
     * @param ruText russian text
     * @return local items
     */
    private static List<LocaleItem> extractLocaleItems(String enText, String ruText) {
        List<LocaleItem> localeItems = new ArrayList<>();

        enText = extractString(enText);
        ruText = extractString(ruText);

        if ((enText != null) && !enText.isEmpty()) {
            localeItems.add(new LocaleItem(
                    Language.ENGLISH.getCode(),
                    extractString(enText)));
        }

        if ((ruText != null) && !ruText.isEmpty()) {
            localeItems.add(new LocaleItem(
                    Language.RUSSIAN.getCode(),
                    extractString(ruText)));
        }

        return localeItems;
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
        final String ENTRY_ID = "3YSoYRePW0OIeaAAkaweE6";
        long id = speakerMap.values().stream()
                .map(Speaker::getId)
                .max(Long::compare)
                .orElse(-1L);

        if (ConferenceSpaceInfo.HOLY_JS_SPACE_INFO.equals(conferenceSpaceInfo)) {
            speakerMap.put(ENTRY_ID, new Speaker(
                    ++id,
                    "https://images.ctfassets.net/nn534z2fqr9f/32Ps6pruAEsOag6g88oSMa/c71710c584c7933020e4f96c2382427a/IMG_4618.JPG",
                    Collections.singletonList(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "Irina Shestak")),
                    Collections.emptyList(),
                    Collections.singletonList(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "tl;dr javascript, wombats and hot takes. Irina is a London via Vancouver software developer. She spends quite a bit of her time exploring the outdoors, gushing over trains, and reading some Beatniks.")),
                    "_lrlna",
                    "lrlna",
                    false,
                    false
            ));
            entryErrorSet.remove(ENTRY_ID);
        }
    }

    public static void main(String[] args) {
        List<String> locales = getLocales();
        log.info("Locales: {}, {}", locales.size(), locales);

        log.info("Event types");

        for (String locale : locales) {
            List<EventType> eventTypes = getEventTypes(locale);
            log.info("Event types (locale: {}): {}, {}", locale, eventTypes.size(), eventTypes);
        }

        log.info("Events");

        for (String locale : locales) {
            List<Event> events = getEvents(locale);
            log.info("Events (locale: {}): {}, {}", locale, events.size(), events);
        }

        for (ConferenceSpaceInfo conferenceSpaceInfo : ConferenceSpaceInfo.values()) {
            log.info("Conference space info: {}", conferenceSpaceInfo);

            List<Speaker> speakers = getSpeakers(conferenceSpaceInfo, null);
            log.info("Speakers: {}, {}", speakers.size(), speakers);

            List<Talk> talks = getTalks(conferenceSpaceInfo, null);
            log.info("Talks: {}, {}", talks.size(), talks);
        }
    }
}
