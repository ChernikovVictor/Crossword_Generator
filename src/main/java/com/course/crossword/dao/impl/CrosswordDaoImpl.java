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
    public Optional<Crossword> getById(String id, String login) {
        List<Crossword> crosswords = getCrosswordsForUser(login);
        return crosswords.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public void save(Crossword crossword, String login) throws IOException {
        String filepath = CROSSWORDS_URL + PATH_SEPARATOR + login + PATH_SEPARATOR + crossword.getName() + JSON_EXTENSION;
        jsonLoader.saveAsJson(crossword, filepath);
    }

    @Override
    public List<Crossword> getCrosswordsForUser(String login) {
        try {
            String path = CROSSWORDS_URL + PATH_SEPARATOR + login;
            List<String> filenames = FileUtils.getAllFileNamesByPath(path);
            List<Crossword> result = new ArrayList<>();
            for (String filename : filenames) {
                Crossword crossword = jsonLoader.loadFromJson(path + PATH_SEPARATOR + filename);
                result.add(crossword);
            }
            return result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public void createDirectoryForUserCrosswords(String login) throws IOException {
        String path = CROSSWORDS_URL + PATH_SEPARATOR + login;
        boolean isCreated = FileUtils.createDirectory(path);
        if (!isCreated) {
            throw new IOException("Не удалось создать директорию для хранения кроссвордов пользователя " + login);
        }
    }
}
