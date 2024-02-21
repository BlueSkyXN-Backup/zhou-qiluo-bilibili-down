package com.example.bili.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Bili {
    private Integer id;
    private String date;
    private Integer count;
}
