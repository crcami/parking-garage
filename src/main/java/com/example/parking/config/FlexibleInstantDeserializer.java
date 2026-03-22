package com.example.parking.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

public class FlexibleInstantDeserializer extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        String text = parser.getText();
        try {
            return Instant.parse(text);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(text).toInstant(ZoneOffset.UTC);
        }
    }
}