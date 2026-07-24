package bg.softuni.cinevaultrecommendationservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RecommendationRequestDto {
    @NotNull(message = "User ID is required.")
    private UUID userId;
    @NotEmpty(message = "At least one watched movie is required.")
    @Valid
    private List<MoviePreferenceDto> moviePreferences;
}
