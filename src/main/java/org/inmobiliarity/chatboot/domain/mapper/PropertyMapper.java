package org.inmobiliarity.chatboot.domain.mapper;

import org.inmobiliarity.chatboot.domain.model.Property;

import java.util.Map;

public class PropertyMapper {

    public static Property toDomain(Map.Entry<String, Property> entry) {
        Property dto = entry.getValue();

        return Property.builder()
                .idProperty(dto.getIdProperty())
                .regionLabel(dto.getRegionLabel())
                .cityLabel(dto.getCityLabel())
                .zoneLabel(dto.getZoneLabel())
                .nameCurrency(dto.getNameCurrency())
                .address(dto.getAddress())
                .title(dto.getTitle())
                .area(dto.getArea())
                .builtArea(dto.getBuiltArea())
                .privateArea(dto.getPrivateArea())
                .salePriceLabel(dto.getSalePriceLabel())
                .bedrooms(dto.getBedrooms())
                .bathrooms(dto.getBathrooms())
                .garages(dto.getGarages())
                .floor(dto.getFloor())
                .stratum(dto.getStratum())
                .observations(dto.getObservations())
                .propertyConditionLabel(dto.getPropertyConditionLabel())
                .availabilityLabel(dto.getAvailabilityLabel())
                .link(dto.getLink())
                .userData(dto.getUserData() != null ? dto.getUserData() : null)
                .mainImage(dto.getMainImage() != null ?
                        dto.getMainImage() : null)
                .features(dto.getFeatures() != null ?
                        dto.getFeatures() : null)
                .galleries(dto.getGalleries() != null ?
                        dto.getGalleries() : null)
                .build();
    }
}

