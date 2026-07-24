package bg.softuni.cinevaultrecommendationservice.dto;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    private UUID movieId;
    private String reason;
    private Integer score;

}
