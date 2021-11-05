package com.course.crossword.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrosswordNameResponse {

    private String id;
    private String name;
    private boolean isOriginal;

}
