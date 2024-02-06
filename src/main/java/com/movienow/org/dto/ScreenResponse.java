package com.movienow.org.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenResponse {
    private Long theatreId;
    private String theatreName;
    private Long movieId;
    private String movieName;
    Map<Long, ScreenDetails> screens;
}
