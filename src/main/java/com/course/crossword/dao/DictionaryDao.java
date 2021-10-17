package com.course.crossword.dao;

import com.course.crossword.model.dictionary.Dictionary;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DictionaryDao {

    List<Dictionary> getAllDictionaries();

    Optional<Dictionary> findByName(String dictionaryName);

    void save(Dictionary dictionary) throws IOException;

}
