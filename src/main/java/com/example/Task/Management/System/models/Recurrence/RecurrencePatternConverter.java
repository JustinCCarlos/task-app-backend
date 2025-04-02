package com.example.Task.Management.System.models.Recurrence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

public class RecurrencePatternConverter implements AttributeConverter<RecurrencePattern, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public java.lang.String convertToDatabaseColumn(RecurrencePattern pattern) {
        try {
            return pattern == null ? null : objectMapper.writeValueAsString(pattern);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert RecurrencePattern to JSON", e);
        }
    }

    @Override
    public RecurrencePattern convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, RecurrencePattern.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to RecurrencePattern", e);
        }
    }
}
