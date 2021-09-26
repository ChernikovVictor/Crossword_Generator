package com.course.crossword.dao.impl;

import com.course.crossword.dao.CrosswordDao;
import com.course.crossword.model.crossword.Crossword;
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
public class CrosswordDaoImpl implements CrosswordDao {

    private final String CROSSWORDS_URL = STORAGE_URL + "/crosswords";

    private final JsonLoader<Crossword> jsonLoader = new JsonLoader<>(Crossword.class);

    @Override
    public List<Crossword> getAllCrosswords() {
        try {
            List<String> filenames = FileUtils.getAllFileNamesByPath(CROSSWORDS_URL);
            List<Crossword> result = new ArrayList<>();
            for (String filename : filenames) {
                Crossword crossword = jsonLoader.loadFromJson(CROSSWORDS_URL + PATH_SEPARATOR + filename);
                result.add(crossword);
            }
            return result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Crossword> getById(String id) {
        List<Crossword> crosswords = getAllCrosswords();
        return crosswords.stream().filter(c -> c.getId().equals(id)).findFirst();
    }
}
