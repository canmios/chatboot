package org.inmobiliarity.chatboot.domain.service;

import org.apache.hc.core5.http.ParseException;
import org.inmobiliarity.chatboot.domain.model.Property;
import org.inmobiliarity.chatboot.infrastructure.PropertyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

@Service
public class ChatService {

    @Value("${cohere.api.key}")
    private String apiKey;

    private final PropertyRepository propertyRepository;

    public ChatService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public String processUserMessage(String message) throws IOException, ParseException {
        if (message.toLowerCase().contains("price range")) {
            return searchProperties(message);
        }

        return getCohereResponse(message);
    }

    private String getCohereResponse(String message) throws IOException, ParseException {
        String apiUrl = "https://api.cohere.ai/generate";
        JSONObject requestBody = new JSONObject()
                .put("prompt", message)
                .put("model", "command-xlarge-nightly")
                .put("max_tokens", 200);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiUrl);
            request.addHeader("Authorization", "Bearer " + apiKey);
            request.addHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(requestBody.toString()));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());
                System.out.println("Response from Cohere: " + responseString);

                JSONObject jsonResponse = new JSONObject(responseString);

                // Check if the response contains a "text" field and return it
                if (jsonResponse.has("text")) {
                    return jsonResponse.getString("text").trim();
                } else {
                    return "Unexpected response format: " + responseString;
                }
            }
        }
    }

    private String searchProperties(String message) throws IOException, ParseException {
        double[] priceRange = extractPriceRangeFromCohere(message);
        double minPrice = priceRange[0];
        double maxPrice = priceRange[1];

        List<Property> properties = propertyRepository.findByPriceBetween(minPrice, maxPrice);

        StringBuilder response = new StringBuilder("Here are some properties in the price range:\n");
        for (Property property : properties) {
            response.append(property.getAddress()).append(", ")
                    .append(property.getCity()).append(" - $")
                    .append(property.getPrice()).append("\n");
        }

        return response.toString();
    }

    private double[] extractPriceRangeFromCohere(String message) throws IOException, ParseException {
        String prompt = String.format("Extract the price range from the following message: \"%s\"", message);
        String apiUrl = "https://api.cohere.ai/generate";
        JSONObject requestBody = new JSONObject()
                .put("prompt", prompt)
                .put("model", "command-xlarge-nightly")
                .put("max_tokens", 100);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiUrl);
            request.addHeader("Authorization", "Bearer " + apiKey);
            request.addHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(requestBody.toString()));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());
                JSONObject jsonResponse = new JSONObject(responseString);

                // Handle response accordingly
                if (jsonResponse.has("text")) {
                    String[] prices = jsonResponse.getString("text").trim().split("-");
                    double minPrice = Double.parseDouble(prices[0].replaceAll("[^\\d.]", ""));
                    double maxPrice = Double.parseDouble(prices[1].replaceAll("[^\\d.]", ""));

                    return new double[]{minPrice, maxPrice};
                } else {
                    throw new ParseException("Unexpected response format: " + responseString);
                }
            }
        }
    }

    public void saveProperty(Property property) {
        propertyRepository.save(property);
    }
}
