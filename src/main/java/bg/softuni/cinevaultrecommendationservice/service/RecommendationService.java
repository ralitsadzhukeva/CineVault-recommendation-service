package bg.softuni.cinevaultrecommendationservice.service;

import bg.softuni.cinevaultrecommendationservice.dto.RecommendationDto;
import bg.softuni.cinevaultrecommendationservice.dto.RecommendationRequestDto;

import java.util.List;
import java.util.UUID;

public interface RecommendationService {

    List<RecommendationDto> getRecommendations(UUID userId);

    void generateRecommendations(RecommendationRequestDto request);

    void regenerateRecommendations(RecommendationRequestDto request);

    void deleteRecommendations(UUID userId);

}
