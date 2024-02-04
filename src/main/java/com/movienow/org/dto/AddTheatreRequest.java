package com.movienow.org.dto;

import lombok.Data;

@Data
public class AddTheatreRequest {
    private String name;
    private AddressRequest address;
}
