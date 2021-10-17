package com.course.crossword.dao.impl;

import com.course.crossword.dao.DictionaryDao;
import com.course.crossword.model.dictionary.Dictionary;
import com.course.crossword.util.FileUtils;
import com.course.crossword.util.JsonLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.course.crossword.model.Constants.*;

@Slf4j
@Component
public class DictionaryDaoImpl implements DictionaryDao {

    private final String DICTIONARIES_URL = STORAGE_URL + "/dictionaries";

    private final JsonLoader<Dictionary> jsonLoader = new JsonLoader<>(Dictionary.class);

    @Override
    public List<Dictionary> getAllDictionaries() {
        try {
            List<String> filenames = FileUtils.getAllFileNamesByPath(DICTIONARIES_URL);
            List<Dictionary> result = new ArrayList<>();
            for (String filename : filenames) {
                Dictionary dictionary = jsonLoader.loadFromJson(DICTIONARIES_URL + PATH_SEPARATOR + filename);
                result.add(dictionary);
            }
            return result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Dictionary> findByName(String dictionaryName) {
        List<Dictionary> dictionaries = getAllDictionaries();
        return dictionaries.stream().filter(d -> d.getName().equals(dictionaryName)).findFirst();
    }

    @Override
    public void save(Dictionary dictionary) throws IOException {
        String filepath = DICTIONARIES_URL + PATH_SEPARATOR + dictionary.getName() + JSON_EXTENSION;
        jsonLoader.saveAsJson(dictionary, filepath);
    }
}
