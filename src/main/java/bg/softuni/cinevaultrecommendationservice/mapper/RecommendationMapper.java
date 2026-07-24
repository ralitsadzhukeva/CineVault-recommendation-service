package bg.softuni.cinevaultrecommendationservice.mapper;

import bg.softuni.cinevaultrecommendationservice.dto.RecommendationDto;
import bg.softuni.cinevaultrecommendationservice.model.Recommendation;
import org.springframework.stereotype.Component;

@Component
public class RecommendationMapper {

    public RecommendationDto toDto(Recommendation recommendation) {
        return RecommendationDto.builder()
                .movieId(recommendation.getMovieId())
                .reason(recommendation.getReason())
                .score(recommendation.getScore())
                .build();
    }
}
