package com.movienow.org.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetailsDto {
    private String movieName;
    private Map<Long,ScreenDetails> screens = new HashMap<>();
}
