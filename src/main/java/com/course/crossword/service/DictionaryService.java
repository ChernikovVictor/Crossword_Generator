package com.course.crossword.service;

import com.course.crossword.dto.DictionaryDTO;
import com.course.crossword.exceptions.ValidationException;
import com.course.crossword.model.dictionary.Word;

import java.io.IOException;
import java.util.List;

public interface DictionaryService {

    void addWord(Word word, String dictionaryName) throws ValidationException, IOException;

    List<String> getAllDictionaryNames();

    void removeWord(String wordValue, String dictionaryName) throws IOException;

    void updateWord(Word word, String dictionaryName) throws IOException;

    DictionaryDTO getWords(String dictionaryName, int page, String filter, String sort, String sortDirection);
}
