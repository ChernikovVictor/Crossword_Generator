package com.course.crossword.dao;

import com.course.crossword.model.crossword.Crossword;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CrosswordDao {

    List<Crossword> getCrosswordsForUser(String login);

    Optional<Crossword> getById(String id, String login);

    void save(Crossword crossword, String login) throws IOException;

    void createDirectoryForUserCrosswords(String login) throws IOException;
}
