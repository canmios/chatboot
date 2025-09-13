package org.inmobiliarity.chatboot.application.controller;

import lombok.AllArgsConstructor;
import org.inmobiliarity.chatboot.application.request.PropertyRequest;
import org.inmobiliarity.chatboot.domain.model.Property;
import org.inmobiliarity.chatboot.domain.service.PropertyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ChatController {

    private final PropertyService propertyService;

    @GetMapping("/properties/search")
    public Flux<Property> searchProperties(@RequestBody PropertyRequest propertyRequest) {
        return propertyService.getFilteredProperties(propertyRequest);
    }

}
