package com.example.parking.controller;

import com.example.parking.dto.revenue.RevenueRequest;
import com.example.parking.dto.revenue.RevenueResponse;
import com.example.parking.service.RevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Exposes the revenue query endpoint. */
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
    public RevenueResponse getRevenue(@Valid @RequestBody RevenueRequest request) {
        return revenueService.getRevenue(request.date(), request.sector());
    }
}