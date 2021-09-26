package com.course.crossword.model.crossword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cell implements Serializable {

    private String id;
    private boolean active;
    private boolean visible;
    private String value;
    private List<String> definitions;

}
