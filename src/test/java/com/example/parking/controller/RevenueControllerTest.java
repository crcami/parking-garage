package com.example.parking.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.parking.dto.revenue.RevenueResponse;
import com.example.parking.service.RevenueService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Integration tests for the revenue endpoint. */
@WebMvcTest(RevenueController.class)
class RevenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RevenueService revenueService;

    /** Verifies that an invalid date format returns HTTP 400. */
    @Test
    void getRevenue_shouldReturn400ForInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/revenue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"date": "21/03/2026", "sector": "A"}
                        """))
                .andExpect(status().isBadRequest());
    }

    /** Verifies that a valid request returns HTTP 200 with the revenue payload. */
    @Test
    void getRevenue_shouldReturn200ForValidRequest() throws Exception {
        RevenueResponse response = new RevenueResponse(
                new BigDecimal("81.00"),
                "BRL",
                Instant.parse("2026-03-21T13:00:00Z"));

        when(revenueService.getRevenue(eq(LocalDate.of(2026, 3, 21)), eq("A")))
                .thenReturn(response);

        mockMvc.perform(get("/revenue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"date": "2026-03-21", "sector": "A"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(81.00))
                .andExpect(jsonPath("$.currency").value("BRL"));
    }

    /** Verifies that a missing sector returns HTTP 400. */
    @Test
    void getRevenue_shouldReturn400WhenSectorIsBlank() throws Exception {
        mockMvc.perform(get("/revenue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"date": "2026-03-21", "sector": ""}
                        """))
                .andExpect(status().isBadRequest());
    }

    /** Verifies that a missing body returns HTTP 400. */
    @Test
    void getRevenue_shouldReturn400WhenBodyIsMissing() throws Exception {
        mockMvc.perform(get("/revenue")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}