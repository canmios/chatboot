package org.inmobiliarity.chatboot.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class Features {
    private List<Feature> internal;
    private List<Feature> external;
}
