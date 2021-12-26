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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                String originalValue = (cells[i][j].getOriginalValue() == null) ? "-" : cells[i][j].getOriginalValue();
                builder.append(originalValue).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
