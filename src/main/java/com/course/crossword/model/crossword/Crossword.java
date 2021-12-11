package com.course.crossword.model.crossword;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;

@Data
@NoArgsConstructor
public class Crossword implements Serializable {

    private String id;
    private String name;
    private String dictionaryName;
    private int hints;
    private Cell[][] cells;

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i<cells.length;i++){
            for(int j = 0;j<cells[0].length;j++){
                builder.append(cells[i][j].getValue() == null ? "-" : cells[i][j].getValue()).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
