package bg.softuni.cinevaultrecommendationservice.controller;

import bg.softuni.cinevaultrecommendationservice.dto.RecommendationRequestDto;
import bg.softuni.cinevaultrecommendationservice.service.RecommendationService;
import bg.softuni.cinevaultrecommendationservice.web.RecommendationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationService recommendationService;

    @Test
    @WithMockUser
    void getRecommendations_shouldReturnRecommendations() throws Exception {

        UUID userId = UUID.randomUUID();

        when(recommendationService.getRecommendations(userId))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/recommendations/user/{userId}", userId)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(recommendationService)
                .getRecommendations(userId);
    }

    @Test
    @WithMockUser
    void generateRecommendations_shouldReturnCreated() throws Exception {

        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();

        String requestJson = """
            {
                "userId": "%s",
                "watchedMovies": [],
                "allMovies": [
                    {
                        "movieId": "%s",
                        "genre": "ACTION",
                        "averageRating": 8.5
                    }
                ]
            }
            """.formatted(userId, movieId);

        mockMvc.perform(post("/api/recommendations/generate")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        verify(recommendationService)
                .generateRecommendations(any(RecommendationRequestDto.class));
    }


    @Test
    @WithMockUser
    void deleteRecommendations_shouldReturnNoContent() throws Exception {

        UUID userId = UUID.randomUUID();

        doNothing().when(recommendationService).deleteRecommendations(userId);

        mockMvc.perform(delete("/api/recommendations/user/{userId}", userId).with(csrf()))
                .andExpect(status().isNoContent());

        verify(recommendationService).deleteRecommendations(userId);
    }


    @Test
    void getRecommendations_shouldRequireAuthentication() throws Exception {

        UUID userId = UUID.randomUUID();

        mockMvc.perform(get("/api/recommendations/user/{userId}", userId))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void generateRecommendations_shouldRequireAuthentication() throws Exception {

        UUID userId = UUID.randomUUID();

        String requestJson = """
                {
                    "userId": "%s",
                    "watchedMovies": [],
                    "allMovies": []
                }
                """.formatted(userId);

        mockMvc.perform(post("/api/recommendations/generate")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void deleteRecommendations_shouldRequireAuthentication() throws Exception {

        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/recommendations/user/{userId}", userId)
                                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser
    void generateRecommendations_shouldRejectInvalidRequest() throws Exception {

        String invalidRequestJson = """
                {
                    "userId": null,
                    "watchedMovies": [],
                    "allMovies": []
                }
                """;

        mockMvc.perform(post("/api/recommendations/generate")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }
}