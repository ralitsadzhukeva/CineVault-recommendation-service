package bg.softuni.cinevaultrecommendationservice.dto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    private UUID movieId;
    private String reason;
    @NotNull
    private Integer score;

}
