package bg.softuni.cinevaultrecommendationservice.dto;

import bg.softuni.cinevaultrecommendationservice.model.Genre;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class MoviePreferenceDto {
    @NotNull
    private UUID movieId;
    @NotNull
    private Genre genre;
    @NotNull
    @Min(1)
    @Max(10)
    private Integer rating;
    private boolean watched;
}
