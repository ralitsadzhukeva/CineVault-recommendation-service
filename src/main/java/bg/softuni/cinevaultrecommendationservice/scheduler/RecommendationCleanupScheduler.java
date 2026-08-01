package bg.softuni.cinevaultrecommendationservice.scheduler;

import bg.softuni.cinevaultrecommendationservice.repository.RecommendationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class RecommendationCleanupScheduler {
    private final RecommendationRepository recommendationRepository;

    public RecommendationCleanupScheduler(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @Scheduled(fixedDelayString = "PT24H")
    @Transactional
    public void deleteOldRecommendations() {
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(7);

        log.info("Starting recommendation cleanup. Removing recommendations older than {}", expirationDate);

        int deletedCount = recommendationRepository.deleteByCreatedOnBefore(expirationDate);

        log.info("Recommendation cleanup completed. Deleted {} old recommendations.", deletedCount);
    }
}
