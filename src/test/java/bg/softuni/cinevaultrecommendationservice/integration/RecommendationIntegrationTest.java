package bg.softuni.cinevaultrecommendationservice.integration;

import bg.softuni.cinevaultrecommendationservice.model.Recommendation;
import bg.softuni.cinevaultrecommendationservice.repository.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RecommendationIntegrationTest {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @BeforeEach
    void setUp() {
        recommendationRepository.deleteAll();
    }

    @Test
    void recommendation_shouldBeSavedAndRetrieved() {

        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();

        Recommendation recommendation = Recommendation.builder()
                .userId(userId)
                .movieId(movieId)
                .reason("Because you highly rated SCI_FI movies.")
                .score(90)
                .createdOn(LocalDateTime.now())
                .build();

        Recommendation saved = recommendationRepository.save(recommendation);

        List<Recommendation> recommendations =
                recommendationRepository.findByUserId(userId);

        assertFalse(recommendations.isEmpty());

        Recommendation found = recommendations.get(0);

        assertNotNull(saved.getId());
        assertEquals(userId, found.getUserId());
        assertEquals(movieId, found.getMovieId());
        assertEquals(90, found.getScore());
        assertEquals("Because you highly rated SCI_FI movies.", found.getReason());
    }
}