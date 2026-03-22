package com.example.flowforge.movie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieMetadata {
    private String id;
    private String title;
    private String overview;
    private int releaseYear;
}
