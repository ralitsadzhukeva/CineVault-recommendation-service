package bg.softuni.cinevaultrecommendationservice.repository;

import bg.softuni.cinevaultrecommendationservice.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
    List<Recommendation> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    boolean existsByUserIdAndMovieId(UUID userId, UUID movieId);

    boolean existsByUserId(UUID userId);
}
