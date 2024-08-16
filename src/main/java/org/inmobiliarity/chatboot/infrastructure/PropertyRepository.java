package org.inmobiliarity.chatboot.infrastructure;


import org.inmobiliarity.chatboot.domain.model.Property;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PropertyRepository extends MongoRepository<Property, String> {
    List<Property> findByPriceBetween(double minPrice, double maxPrice);
}
