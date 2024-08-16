package org.inmobiliarity.chatboot.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "properties")
public class Property {

    @Id
    private String id;
    private String address;
    private String city;
    private double price;

}
