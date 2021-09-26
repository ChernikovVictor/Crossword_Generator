package com.course.crossword.dao;

import com.course.crossword.model.crossword.Crossword;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CrosswordDao {

    List<Crossword> getAllCrosswords();

    Optional<Crossword> getById(String id);

    void save(Crossword crossword) throws IOException;
}
