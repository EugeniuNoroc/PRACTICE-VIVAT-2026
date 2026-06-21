package com.university.tracker.dto;

import java.util.UUID;

public record ProjectSummaryDto(UUID id, String name, Long taskCount) {
}
