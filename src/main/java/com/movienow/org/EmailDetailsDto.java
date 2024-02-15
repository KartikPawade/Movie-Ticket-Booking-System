package com.movienow.org;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetailsDto {
    String email;
    Double price;
    List<Long> seatIds;
}
