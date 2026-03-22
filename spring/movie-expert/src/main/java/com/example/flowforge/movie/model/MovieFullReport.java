package com.example.flowforge.movie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieFullReport {
    private MovieMetadata metadata;
    private CastData castInfo;
    private RatingData ratings;
    private String generatedAt;
}
