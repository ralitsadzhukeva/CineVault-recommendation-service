package bg.softuni.cinevaultrecommendationservice.web;

import bg.softuni.cinevaultrecommendationservice.dto.RecommendationDto;
import bg.softuni.cinevaultrecommendationservice.dto.RecommendationRequestDto;
import bg.softuni.cinevaultrecommendationservice.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/user/{userId}")
    public List<RecommendationDto> getRecommendations(@PathVariable UUID userId) {
        return recommendationService.getRecommendations(userId);
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public void generateRecommendations(@Valid @RequestBody RecommendationRequestDto request) {

        recommendationService.generateRecommendations(request);
    }

    @DeleteMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecommendations(@PathVariable UUID userId) {

        recommendationService.deleteRecommendations(userId);
    }
}
