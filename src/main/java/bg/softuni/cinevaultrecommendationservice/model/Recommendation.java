package bg.softuni.cinevaultrecommendationservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recommendations")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")

    private UUID id;

    @NotNull
    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;
    @NotNull
    @Column(columnDefinition = "BINARY(16)")
    private UUID movieId;
    @NotBlank
    private String reason;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer score;
    private LocalDateTime createdOn;
}


