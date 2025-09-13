package org.inmobiliarity.chatboot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Property {
    private long idProperty;
    private String regionLabel;
    private String cityLabel;
    private String zoneLabel;
    private String nameCurrency;
    private String address;
    private String title;
    private String area;
    private String builtArea;
    private String privateArea;
    private String salePriceLabel;
    private String bedrooms;
    private String bathrooms;
    private String garages;
    private String floor;
    private String stratum;
    private String observations;
    private String propertyConditionLabel;
    private String link;
    private String availabilityLabel;
    private Gallery galleries;
    private MainImage mainImage;
    private Features features;
    private UserData userData;
}
