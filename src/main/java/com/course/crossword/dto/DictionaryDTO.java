package com.course.crossword.dto;

import com.course.crossword.model.dictionary.Word;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DictionaryDTO {

    private List<Word> data;
    private long total;

}
