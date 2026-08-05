package bg.softuni.cinevaultrecommendationservice.service;

import bg.softuni.cinevaultrecommendationservice.dto.MovieDto;
import bg.softuni.cinevaultrecommendationservice.dto.MoviePreferenceDto;
import bg.softuni.cinevaultrecommendationservice.dto.RecommendationDto;
import bg.softuni.cinevaultrecommendationservice.dto.RecommendationRequestDto;
import bg.softuni.cinevaultrecommendationservice.exception.RecommendationNotFoundException;
import bg.softuni.cinevaultrecommendationservice.mapper.RecommendationMapper;
import bg.softuni.cinevaultrecommendationservice.model.Genre;
import bg.softuni.cinevaultrecommendationservice.model.Recommendation;
import bg.softuni.cinevaultrecommendationservice.repository.RecommendationRepository;
import bg.softuni.cinevaultrecommendationservice.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RecommendationMapper recommendationMapper;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private Recommendation recommendation;
    private RecommendationDto recommendationDto;

    @BeforeEach
    void setUp(){
        recommendation = Recommendation.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .movieId(UUID.randomUUID())
                .reason("Because you enjoy Sci-Fi movies.")
                .score(95)
                .createdOn(LocalDateTime.now())
                .build();

        recommendationDto = RecommendationDto.builder()
                .movieId(recommendation.getMovieId())
                .reason(recommendation.getReason())
                .score(recommendation.getScore())
                .build();
    }

    @Test
    void getRecommendations_shouldSortByScoreAndLimitTo12() {

        UUID userId = UUID.randomUUID();

        List<Recommendation> recommendations = new ArrayList<>();

        for (int i = 1; i <= 13; i++) {
            recommendations.add(
                    Recommendation.builder()
                            .id(UUID.randomUUID())
                            .userId(userId)
                            .movieId(UUID.randomUUID())
                            .reason("Recommendation " + i)
                            .score(i)
                            .createdOn(LocalDateTime.now())
                            .build()
            );
        }

        when(recommendationRepository.findByUserId(userId))
                .thenReturn(recommendations);

        when(recommendationMapper.toDto(any(Recommendation.class)))
                .thenAnswer(invocation -> {
                    Recommendation r = invocation.getArgument(0);
                    return RecommendationDto.builder()
                            .movieId(r.getMovieId())
                            .reason(r.getReason())
                            .score(r.getScore())
                            .build();
                });

        List<RecommendationDto> result =
                recommendationService.getRecommendations(userId);

        assertEquals(12, result.size());

        assertEquals(13, result.get(0).getScore());
        assertEquals(12, result.get(1).getScore());
        assertEquals(2, result.get(11).getScore());

        verify(recommendationRepository)
                .findByUserId(userId);

        verify(recommendationMapper, times(12))
                .toDto(any(Recommendation.class));
    }

    @Test
    void generateRecommendations_shouldGenerateNewRecommendations() {

        UUID userId = recommendation.getUserId();
        UUID movieId = recommendation.getMovieId();

        MoviePreferenceDto moviePreferenceDto = MoviePreferenceDto.builder()
                .movieId(movieId)
                .rating(9)
                .watched(true)
                .genre(Genre.SCIFI)
                .build();

        MovieDto similarMovie = MovieDto.builder()
                .movieId(UUID.randomUUID())
                .genre(Genre.SCIFI)
                .averageRating(9.0)
                .build();

        RecommendationRequestDto request = RecommendationRequestDto.builder()
                .userId(userId)
                .watchedMovies(List.of(moviePreferenceDto))
                .allMovies(List.of(similarMovie))
                .build();

        when(recommendationRepository.saveAll(anyList()))
                .thenReturn(List.of());

        recommendationService.generateRecommendations(request);

        verify(recommendationRepository)
                .deleteAllByUserId(userId);

        verify(recommendationRepository)
                .saveAll(anyList());
    }
    @Test
    void getRecommendations_shouldReturnEmptyList_whenNoRecommendationsExist() {
        UUID userId = UUID.randomUUID();

        when(recommendationRepository.findByUserId(userId))
                .thenReturn(List.of());

        List<RecommendationDto> result = recommendationService.getRecommendations(userId);

        assertEquals(0, result.size());

        verify(recommendationRepository).findByUserId(userId);

        verify(recommendationMapper, never())
                .toDto(any(Recommendation.class));
    }
    @Test
    void generateRecommendations_shouldReturn_whenNoWatchedMovies() {

        UUID userId = UUID.randomUUID();

        RecommendationRequestDto request =
                RecommendationRequestDto.builder()
                        .userId(userId)
                        .watchedMovies(List.of())
                        .allMovies(List.of())
                        .build();

        recommendationService.generateRecommendations(request);

        verify(recommendationRepository)
                .deleteAllByUserId(userId);

        verify(recommendationRepository, never())
                .saveAll(anyList());
    }

    @Test
    void deleteRecommendations_shouldDeleteAllRecommendations() {
        UUID userId = recommendation.getUserId();

        when(recommendationRepository.existsByUserId(userId))
                .thenReturn(true);

        recommendationService.deleteRecommendations(userId);

        verify(recommendationRepository)
                .existsByUserId(userId);

        verify(recommendationRepository)
                .deleteAllByUserId(userId);

        verify(recommendationRepository, never())
                .saveAll(anyList());

        verify(recommendationMapper, never())
                .toDto(any(Recommendation.class));
    }
    @Test
    void deleteRecommendations_shouldThrowException_whenNoRecommendationsExist() {
        UUID userId = recommendation.getUserId();

        when(recommendationRepository.existsByUserId(userId))
                .thenReturn(false);

        assertThrows(RecommendationNotFoundException.class, () -> recommendationService.deleteRecommendations(userId));

        verify(recommendationRepository)
                .existsByUserId(userId);

        verify(recommendationRepository, never())
                .deleteAllByUserId(userId);
    }

    @Test
    void regenerateRecommendations_shouldDeleteAllRecommendationsAndGenerateNewRecommendations() {
        UUID userId = recommendation.getUserId();

        MoviePreferenceDto watchedMovie = MoviePreferenceDto.builder()
                .movieId(recommendation.getMovieId())
                .rating(9)
                .watched(true)
                .genre(Genre.SCIFI)
                .build();

        MovieDto similarMovie = MovieDto.builder()
                .movieId(UUID.randomUUID())
                .genre(Genre.SCIFI)
                .averageRating(9.0)
                .build();

        RecommendationRequestDto request = RecommendationRequestDto.builder()
                .userId(userId)
                .watchedMovies(List.of(watchedMovie))
                .allMovies(List.of(similarMovie))
                .build();

        when(recommendationRepository.saveAll(anyList()))
                .thenReturn(List.of());

        recommendationService.regenerateRecommendations(request);

        verify(recommendationRepository)
                .deleteAllByUserId(userId);
        verify(recommendationRepository)
                .saveAll(anyList());
    }
}


