package bg.softuni.cinevaultrecommendationservice.service.impl;

import bg.softuni.cinevaultrecommendationservice.dto.MovieDto;
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
import java.util.ArrayList;
import java.util.Comparator;
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
        return recommendationRepository.findByUserId(userId)
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    @Override
    public void generateRecommendations(RecommendationRequestDto request) {
        recommendationRepository.deleteAllByUserId(request.getUserId());

        List<Recommendation> recommendations = new ArrayList<>();

        for (MoviePreferenceDto watchedMovie : request.getWatchedMovies()) {
            if (watchedMovie.getRating() >= 8) {

                List<MovieDto> similarMovies = request.getAllMovies()
                        .stream()
                        .filter(movie -> movie.getGenre() == watchedMovie.getGenre())
                        .filter(movie -> !movie.getMovieId().equals(watchedMovie.getMovieId()))
                        .filter(movie ->
                                request.getWatchedMovies()
                                        .stream()
                                        .noneMatch(w -> w.getMovieId().equals(movie.getMovieId())))
                        .sorted(Comparator.comparing(MovieDto::getAverageRating).reversed())
                        .limit(5)
                        .toList();

                for (MovieDto similarMovie : similarMovies) {
                    boolean alreadyAdded = recommendations
                            .stream()
                            .anyMatch(r -> r.getMovieId()
                                    .equals(similarMovie.getMovieId()));

                    if (alreadyAdded) {
                        continue;
                    }
                    Recommendation recommendation = Recommendation.builder()
                            .userId(request.getUserId())
                            .movieId(similarMovie.getMovieId())
                            .score(calculateRecommendationScore(similarMovie.getAverageRating(), watchedMovie.getRating()))
                            .reason("Because you highly rated " + watchedMovie.getGenre() + " movies.")
                            .createdOn(LocalDateTime.now())
                            .build();
                    recommendations.add(recommendation);
                }
            }
        }
        recommendationRepository.saveAll(recommendations);
    }

    @Override
    public void regenerateRecommendations(RecommendationRequestDto request) {
        recommendationRepository.deleteAllByUserId(request.getUserId());

        generateRecommendations(request);
    }

    @Override
    public void deleteRecommendations(UUID userId) {
        if(!recommendationRepository.existsByUserId(userId)){
            throw new RecommendationNotFoundException(userId);
        }
        recommendationRepository.deleteAllByUserId(userId);
    }
    private Integer calculateRecommendationScore(Double averageRating, Integer userRating) {
        if (averageRating == null) {
            averageRating = 0.0;
        }
        double score = averageRating * userRating;

        return Math.min((int) Math.round(score), 100);
    }
}