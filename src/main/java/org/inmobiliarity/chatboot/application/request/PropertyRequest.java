package org.inmobiliarity.chatboot.application.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyRequest {

    private String match;
    private String location;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer garages;
    private String propertyCondition;
    private Long budget;
    private Integer privateArea;
    private Integer builtArea;
    private Integer area;
    private String observation;
}
