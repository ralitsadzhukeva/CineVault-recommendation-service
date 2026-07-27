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
                        .sorted(
                                Comparator.comparing(MovieDto::getAverageRating,
                                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
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
                            .score(calculateRecommendationScore(similarMovie, watchedMovie))
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
    private Integer calculateRecommendationScore(MovieDto movie, MoviePreferenceDto watchedMovie) {

        double score = 0;

        // Same genre
        if (movie.getGenre() == watchedMovie.getGenre()) {
            score += 70;
        }

        // User really liked the watched movie
        if (watchedMovie.getRating() >= 9) {
            score += 15;
        } else if (watchedMovie.getRating() >= 8) {
            score += 10;
        }

        // Movie popularity
        if (movie.getAverageRating() != null) {
            score += movie.getAverageRating() * 2;
        }

        return (int) Math.min(score, 100);
    }
}