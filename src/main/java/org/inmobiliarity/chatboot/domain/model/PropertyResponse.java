package org.inmobiliarity.chatboot.domain.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyResponse {

    private Map<String, Property> properties = new HashMap<>();
    private int total;
    private String status;

    @JsonAnySetter
    public void setDynamicProperty(String key, Object value) {
        if (key.matches("\\d+") && value instanceof Map<?, ?> valueMap) {
            Property property = new Property();

            property.setIdProperty(Long.parseLong(String.valueOf(valueMap.get("id_property"))));
            property.setRegionLabel((String.valueOf(valueMap.get("region_label"))));
            property.setCityLabel(String.valueOf(valueMap.get("city_label")));
            property.setZoneLabel(String.valueOf(valueMap.get("zone_label")));
            property.setNameCurrency(String.valueOf(valueMap.get("name_currency")));
            property.setAddress(String.valueOf(valueMap.get("address")));
            property.setTitle(String.valueOf(valueMap.get("title")));
            property.setAddress(String.valueOf(valueMap.get("area")));
            property.setArea(String.valueOf(valueMap.get("area")));
            property.setBuiltArea(String.valueOf(valueMap.get("built_area")));
            property.setPrivateArea(String.valueOf(valueMap.get("private_area")));
            property.setSalePriceLabel(String.valueOf(valueMap.get("sale_price_label")));
            property.setBedrooms(String.valueOf(valueMap.get("bedrooms")));
            property.setBathrooms(String.valueOf(valueMap.get("bathrooms")));
            property.setGarages(String.valueOf(valueMap.get("garages")));
            property.setFloor(String.valueOf(valueMap.get("floor")));
            property.setStratum(String.valueOf(valueMap.get("stratum")));
            property.setObservations(String.valueOf(valueMap.get("observations")));
            property.setPropertyConditionLabel(String.valueOf(valueMap.get("property_condition_label")));
            property.setAvailabilityLabel(String.valueOf(valueMap.get("availability_label")));
            property.setLink(String.valueOf(valueMap.get("link")));

            if (valueMap.containsKey("main_image")) {
                Object mainImageObj = valueMap.get("main_image");

                if (mainImageObj instanceof Map<?, ?> imageMap) {
                    MainImage image = new MainImage();
                    image.setUrl(String.valueOf(imageMap.get("url")));
                    property.setMainImage(image);
                } else {
                    property.setMainImage(null);
                }
            }

            if (valueMap.containsKey("user_data")) {
                Map<?, ?> userDataMap = (Map<?, ?>) valueMap.get("user_data");
                UserData user = new UserData();
                user.setFirstName(String.valueOf(userDataMap.get("first_name")));
                user.setLastName(String.valueOf(userDataMap.get("last_name")));
                property.setUserData(user);
            }

            // Features (internal & external)
            if (valueMap.containsKey("features")) {
                Map<?, ?> featuresMap = (Map<?, ?>) valueMap.get("features");

                Features features = new Features();
                features.setInternal(parseFeatureList(featuresMap.get("internal")));
                features.setExternal(parseFeatureList(featuresMap.get("external")));

                property.setFeatures(features);
            }

            // Galleries
            if (valueMap.containsKey("galleries") && valueMap.get("galleries") instanceof List<?> galleriesList) {
                List<Gallery> galleries = new ArrayList<>();

                for (Object g : galleriesList) {
                    if (g instanceof Map<?, ?> gMap) {
                        List<ImageData> images = new ArrayList<>();

                        for (Map.Entry<?, ?> entry : gMap.entrySet()) {
                            String k = String.valueOf(entry.getKey());

                            // Claves que son números => contienen imágenes
                            if (k.matches("\\d+") && entry.getValue() instanceof Map<?, ?> imgMap) {
                                ImageData imageData = new ImageData();
                                imageData.setUrlOriginal(String.valueOf(imgMap.get("url_original")));
                                images.add(imageData);
                            }
                        }

                        // Creamos una Gallery con la lista de imágenes (el id lo ignoramos porque tu clase no lo tiene)
                        if (!images.isEmpty()) {
                            Gallery gallery = new Gallery();
                            gallery.setImages(images);
                            galleries.add(gallery);
                        }
                    }
                }

                // ✅ Establece la primera galería si hay al menos una
                if (!galleries.isEmpty()) {
                    property.setGalleries(galleries.getFirst());
                }
            }

            properties.put(key, property);
        }
    }

    private Long toLong(Object value) {
        return value == null ? null : Long.parseLong(value.toString());
    }

    private List<Feature> parseFeatureList(Object listObj) {
        List<Feature> featuresList = new ArrayList<>();
        if (listObj instanceof List<?> rawList) {
            for (Object item : rawList) {
                if (item instanceof Map<?, ?> itemMap) {
                    Feature feature = new Feature();
                    feature.setId(Integer.parseInt(String.valueOf(itemMap.get("id"))));
                    feature.setNombre(String.valueOf(itemMap.get("nombre")));
                    feature.setName(String.valueOf(itemMap.get("name")));
                    feature.setOwn(Boolean.parseBoolean(String.valueOf(itemMap.get("own"))));
                    featuresList.add(feature);
                }
            }
        }
        return featuresList;
    }

}
