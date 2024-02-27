package com.movienow.org.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TheatreDetailsDto {
    private String name;
    private String address;
    private Map<Long,ScreenDetails> screens = new HashMap<>();
}
