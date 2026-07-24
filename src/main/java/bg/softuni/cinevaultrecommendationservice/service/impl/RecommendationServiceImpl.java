package bg.softuni.cinevaultrecommendationservice.service.impl;

import bg.softuni.cinevaultrecommendationservice.dto.MoviePreferenceDto;
import bg.softuni.cinevaultrecommendationservice.dto.RecommendationDto;
import bg.softuni.cinevaultrecommendationservice.dto.RecommendationRequestDto;
import bg.softuni.cinevaultrecommendationservice.exception.RecommendationNotFoundException;
import bg.softuni.cinevaultrecommendationservice.mapper.RecommendationMapper;
import bg.softuni.cinevaultrecommendationservice.model.Recommendation;
import bg.softuni.cinevaultrecommendationservice.repository.RecommendationRepository;
import bg.softuni.cinevaultrecommendationservice.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository, RecommendationMapper recommendationMapper) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationMapper = recommendationMapper;
    }

    @Override
    public List<RecommendationDto> getRecommendations(UUID userId) {
        List<Recommendation> recommendations =
                recommendationRepository.findByUserId(userId);

        if (recommendations.isEmpty()) {
            throw new RecommendationNotFoundException(userId);
        }
        return recommendations.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    @Override
    public void generateRecommendations(RecommendationRequestDto request) {
        recommendationRepository.deleteByUserId(request.getUserId());

        for (MoviePreferenceDto movie : request.getMoviePreferences()) {
            if (movie.getRating()>=8) {
                Recommendation recommendation = Recommendation.builder()
                        .userId(request.getUserId())
                        .movieId(movie.getMovieId())
                        .reason("Because you enjoyed " + movie.getGenre() + " movies.")
                        .score(movie.getRating())
                        .createdOn(LocalDateTime.now())
                        .build();

                recommendationRepository.save(recommendation);
            }
        }
    }

    @Override
    public void regenerateRecommendations(UUID userId) {
        recommendationRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteRecommendations(UUID userId) {
        if(!recommendationRepository.existsByUserId(userId)){
            throw new RecommendationNotFoundException(userId);
        }
        recommendationRepository.deleteByUserId(userId);
    }
}