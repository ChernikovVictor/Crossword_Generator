package com.course.crossword.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrosswordParametersDTO {

    private String name;
    private int height;
    private int width;
    private boolean auto;
    private String dictionary;

}
