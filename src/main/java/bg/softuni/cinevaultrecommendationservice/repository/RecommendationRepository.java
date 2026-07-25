package bg.softuni.cinevaultrecommendationservice.repository;

import bg.softuni.cinevaultrecommendationservice.model.Recommendation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
    List<Recommendation> findByUserId(UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Recommendation r WHERE r.userId = :userId")
    void deleteAllByUserId(UUID userId);

    boolean existsByUserIdAndMovieId(UUID userId, UUID movieId);

    boolean existsByUserId(UUID userId);
}
