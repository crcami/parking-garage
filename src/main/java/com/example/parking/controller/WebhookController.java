package com.example.parking.controller;

import com.example.parking.dto.webhook.WebhookEventRequest;
import com.example.parking.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Exposes the webhook endpoint. */
@RestController
@Tag(name = "Webhook", description = "Endpoints para recebimento de eventos do simulador")
public class WebhookController {

    private final WebhookService webhookService;

    /** Creates the webhook controller. */
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /** Handles incoming simulator events. */
    @Operation(summary = "Recebe eventos do simulador", description = "Processa eventos do tipo ENTRY, PARKED e EXIT.")
    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveEvent(
        @Valid @RequestBody WebhookEventRequest request
    ) {
        webhookService.handle(request);
        return ResponseEntity.ok().build();
    }
}
