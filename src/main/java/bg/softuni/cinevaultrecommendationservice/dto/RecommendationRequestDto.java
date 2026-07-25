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
    @NotNull
    private UUID userId;
    @NotEmpty
    @Valid
    private List<MoviePreferenceDto> watchedMovies;
    @NotEmpty
    @Valid
    private List<MovieDto> allMovies;
}
