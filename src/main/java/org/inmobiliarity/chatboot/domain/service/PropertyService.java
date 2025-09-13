package org.inmobiliarity.chatboot.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inmobiliarity.chatboot.application.request.PropertyRequest;
import org.inmobiliarity.chatboot.domain.mapper.PropertyMapper;
import org.inmobiliarity.chatboot.domain.model.Feature;
import org.inmobiliarity.chatboot.domain.model.Property;
import org.inmobiliarity.chatboot.domain.model.PropertyResponse;
import org.inmobiliarity.chatboot.domain.model.ZoneResponse;
import org.inmobiliarity.chatboot.domain.response.City;
import org.inmobiliarity.chatboot.domain.response.CityResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final WebClient webClient;

    private static final String COMPANY_ID = "11062593";
    private static final String WASI_TOKEN = "TV6i_aGgM_ZpEM_DwIh";
    private static final String CITY_ID_KEY = "cityId";
    private static final String ZONE_ID_KEY = "zoneId";

    public Flux<Property> getFilteredProperties(PropertyRequest request) {
        String location = Optional.ofNullable(request.getLocation()).orElse("").trim();

        return getCityIdByName(location)
                .flatMap(cityId -> {
                    if (!cityId.isBlank()) {
                        return getZoneIdByName(cityId, location)
                                .defaultIfEmpty("")
                                .map(zoneId -> Map.of(CITY_ID_KEY, cityId, ZONE_ID_KEY, zoneId));
                    } else {
                        return getAllCities()
                                .flatMapSequential(city -> getZoneIdByName(city.getIdCity().toString(), location)
                                        .map(zoneId -> Map.of(CITY_ID_KEY, city.getIdCity().toString(), ZONE_ID_KEY, zoneId))
                                        .onErrorResume(e -> {
                                            log.warn("Error searching zone in city {}: {}", city.getName(), e.getMessage());
                                            return Mono.empty();
                                        }))
                                .next()
                                .switchIfEmpty(Mono.just(Map.of(CITY_ID_KEY, "", ZONE_ID_KEY, "")));
                    }
                })
                .flatMapMany(ids -> {
                    String cityId = ids.get(CITY_ID_KEY);
                    String zoneId = ids.get(ZONE_ID_KEY);

                    MultiValueMap<String, String> params = buildBaseParams();
                    params.add("for_rent", "false");
                    params.add("for_sale", "true");

                    if (!cityId.isBlank()) params.add("id_city", cityId);
                    if (!zoneId.isBlank()) params.add("id_zone", zoneId);
                    Optional.ofNullable(request.getMatch()).ifPresent(v -> params.add("match", v));
                    Optional.ofNullable(request.getBedrooms()).ifPresent(v -> params.add("bedrooms", v.toString()));
                    Optional.ofNullable(request.getBathrooms()).ifPresent(v -> params.add("bathrooms", v.toString()));
                    Optional.ofNullable(request.getGarages()).ifPresent(v -> params.add("garages", v.toString()));
                    Optional.ofNullable(request.getPropertyCondition()).ifPresent(v -> params.add("property_condition", v));
                    Optional.ofNullable(request.getBudget()).ifPresent(v -> params.add("max_price", v.toString()));
                    Optional.ofNullable(request.getPrivateArea()).ifPresent(v -> params.add("min_private_area", v.toString()));
                    Optional.ofNullable(request.getBuiltArea()).ifPresent(v -> params.add("min_built_area", v.toString()));
                    Optional.ofNullable(request.getArea()).ifPresent(v -> params.add("min_area", v.toString()));

                    return webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/property/search")
                                    .queryParams(params)
                                    .build())
                            .retrieve()
                            .bodyToMono(PropertyResponse.class)
                            .doOnSubscribe(sub -> log.info("Calling /property/search with params: {}", params))
                            .flatMapMany(response -> Flux.fromIterable(response.getProperties().entrySet()))
                            .map(PropertyMapper::toDomain)
                            .filter(property -> shouldInclude(property, request.getObservation()));
                });
    }

    private Flux<City> getAllCities() {
        MultiValueMap<String, String> params = buildBaseParams();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/location/cities-from-region/2")
                        .queryParams(params)
                        .build())
                .retrieve()
                .bodyToMono(CityResponse.class)
                .doOnSubscribe(sub -> log.info("Calling /location/cities-from-region/2"))
                .flatMapMany(response -> Flux.fromIterable(response.getCities().values()));
    }

    private Mono<String> getCityIdByName(String cityName) {
        String normalizedInput = normalize(cityName);
        MultiValueMap<String, String> params = buildBaseParams();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/location/cities-from-region/2")
                        .queryParams(params)
                        .build())
                .retrieve()
                .bodyToMono(CityResponse.class)
                .doOnSubscribe(sub -> log.info("Looking up city: {}", cityName))
                .flatMapMany(response -> Flux.fromIterable(response.getCities().values()))
                .filter(city -> normalize(city.getName()).contains(normalizedInput))
                .doOnNext(city -> log.debug("Matched city: {}", city.getName()))
                .next()
                .map(city -> city.getIdCity().toString())
                .switchIfEmpty(Mono.just(""));
    }

    private Mono<String> getZoneIdByName(String cityId, String location) {
        String normalizedLocation = normalize(location);
        MultiValueMap<String, String> params = buildBaseParams();
        params.add("distinct", "true");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/location/zones-from-city/" + cityId)
                        .queryParams(params)
                        .build())
                .retrieve()
                .bodyToMono(ZoneResponse.class)
                .doOnSubscribe(sub -> log.info("Looking up zone in cityId={} for location={}", cityId, location))
                .flatMapMany(response -> Flux.fromIterable(response.getZones().values()))
                .filter(zone -> normalize(zone.getName()).contains(normalizedLocation))
                .doOnNext(zone -> log.debug("Matched zone: {}", zone.getName()))
                .next()
                .map(zone -> zone.getIdZone().toString());
    }

    private MultiValueMap<String, String> buildBaseParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id_company", COMPANY_ID);
        params.add("wasi_token", WASI_TOKEN);
        return params;
    }

    private String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    private boolean shouldInclude(Property property, String observation) {
        if (observation == null || observation.isBlank()) {
            return true;
        }
        String normalizedFeatureName = normalize(observation);
        return Stream.concat(
                        property.getFeatures().getInternal().stream(),
                        property.getFeatures().getExternal().stream()
                )
                .map(Feature::getNombre)
                .anyMatch(name -> normalize(name).contains(normalizedFeatureName));
    }
}
