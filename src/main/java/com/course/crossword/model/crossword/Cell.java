package com.course.crossword.model.crossword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cell implements Serializable {

    private String id;
    private boolean active;
    private String value;
    private String originalValue;
    private List<String> definitions = new ArrayList<>();

}
