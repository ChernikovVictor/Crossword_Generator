package com.course.crossword.service;

import com.course.crossword.dto.CrosswordNameResponse;
import com.course.crossword.model.crossword.Crossword;

import java.util.List;

public interface CrosswordService {

    List<CrosswordNameResponse> getCrosswordNamesList();

    Crossword getById(String id);

    void save(Crossword crossword, String crosswordName, String id);

}
