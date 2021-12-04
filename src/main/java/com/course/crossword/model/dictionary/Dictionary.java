package com.course.crossword.model.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dictionary {

    private String name;
    private List<Word> words;

    public void addWord(Word word) {
        if (words == null) {
            words = new ArrayList<>();
        }
        words.add(word);
    }

    public void removeWord(Word word) {
        if (words != null) {
            words.remove(word);
        }
    }
}