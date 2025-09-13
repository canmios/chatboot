package org.inmobiliarity.chatboot.domain.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoneResponse {

    private Map<String, Zone> zones = new HashMap<>();
    private String status;

    @JsonAnySetter
    public void setDynamicZone(String key, Object value) {
        if (key.matches("\\d+") && value instanceof Map<?, ?> valueMap) {
            Zone zone = new Zone();

            zone.setIdCity(Long.parseLong(String.valueOf(valueMap.get("id_city"))));
            System.out.println("city" + valueMap.get("id_city"));
            String idZoneStr = String.valueOf(valueMap.get("id_zone")).split(",")[0].trim();
            zone.setIdZone(Long.parseLong(idZoneStr));
            System.out.println("Zone" + idZoneStr);
            zone.setName(String.valueOf(valueMap.get("name")));

            zones.put(key, zone);
        }
    }
}

