package com.course.crossword.model.crossword;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Crossword implements Serializable {

    private String id;
    private String name;
    private String dictionaryName;
    private int hints;
    private Cell[][] cells;

}
