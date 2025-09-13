package org.inmobiliarity.chatboot.domain.response;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityResponse {

    private Map<String, City> cities = new HashMap<>();
    private String status;

    @JsonAnySetter
    public void setDynamicProperty(String key, Object value) {
        if (key.matches("\\d+") && value instanceof Map<?, ?> valueMap) {
            City city = new City();

            // Safely extract fields from the map
            city.setIdCity(Long.parseLong(String.valueOf(valueMap.get("id_city"))));
            city.setName(String.valueOf(valueMap.get("name")));
            city.setIdRegion(Long.parseLong(String.valueOf(valueMap.get("id_region"))));

            cities.put(key, city);
        }
    }
}