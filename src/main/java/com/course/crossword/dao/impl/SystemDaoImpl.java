package com.course.crossword.dao.impl;

import com.course.crossword.dao.CrosswordDao;
import com.course.crossword.dao.SystemDao;
import com.course.crossword.model.system.Credentials;
import com.course.crossword.util.FileUtils;
import com.course.crossword.util.JsonLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.course.crossword.model.Constants.*;

@Slf4j
@Component
public class SystemDaoImpl implements SystemDao {

    private final String CREDENTIALS_URL = STORAGE_URL + "/credentials";

    private final JsonLoader<Credentials> jsonLoader = new JsonLoader<>(Credentials.class);

    @Override
    public List<Credentials> getAllUsers() {
        try {
            List<String> filenames = FileUtils.getAllFileNamesByPath(CREDENTIALS_URL);
            List<Credentials> result = new ArrayList<>();
            for (String filename : filenames) {
                Credentials cred = jsonLoader.loadFromJson(CREDENTIALS_URL + PATH_SEPARATOR + filename);
                result.add(cred);
            }
            return result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public void save(Credentials credentials) throws IOException {
        String filepath = CREDENTIALS_URL + PATH_SEPARATOR + credentials.getLogin() + JSON_EXTENSION;
        jsonLoader.saveAsJson(credentials, filepath);
        CrosswordDao crosswordDao = new CrosswordDaoImpl();
        crosswordDao.createDirectoryForUserCrosswords(credentials.getLogin());
    }

}
