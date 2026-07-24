package bg.softuni.cinevaultrecommendationservice.exception;

import java.util.UUID;

public class RecommendationNotFoundException extends RuntimeException {

    public RecommendationNotFoundException(UUID id) {
        super("Recommendation with id " + id + " was not found");
    }
}