package com.example.parking.controller;

import com.example.parking.dto.revenue.RevenueResponse;
import com.example.parking.service.RevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Exposes the revenue query endpoint. */
@Validated
@RestController
@Tag(name = "Receita", description = "Endpoints para consulta de receita da garagem")
public class RevenueController {

    private final RevenueService revenueService;

    /** Creates the revenue controller. */
    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    /** Returns the revenue for a sector and date. */
    @Operation(summary = "Consulta a receita por setor e data", description = "Retorna o valor total cobrado de sessões encerradas em um dado setor na data informada.")
    @GetMapping("/revenue")
    public RevenueResponse getRevenue(
        @Parameter(description = "Data no formato ISO-8601 (yyyy-MM-dd)", example = "2026-03-21",
            schema = @Schema(type = "string", format = "date"))
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,
        @RequestParam @NotBlank String sector
    ) {
        return revenueService.getRevenue(date, sector);
    }
}
