package com.course.crossword.service.impl;

import com.course.crossword.dao.CrosswordDao;
import com.course.crossword.dto.CrosswordNameResponse;
import com.course.crossword.model.crossword.Crossword;
import com.course.crossword.service.CrosswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CrosswordServiceImpl implements CrosswordService {

    private CrosswordDao crosswordDao;

    @Autowired
    public CrosswordServiceImpl(CrosswordDao crosswordDao) {
        this.crosswordDao = crosswordDao;
    }

    @Override
    public List<CrosswordNameResponse> getCrosswordNamesList() {
        List<Crossword> crosswords = crosswordDao.getAllCrosswords();
        return crosswords.stream()
                .map(c -> new CrosswordNameResponse(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Crossword getById(String id) {
        return crosswordDao.getById(id).orElseThrow(() -> new RuntimeException("Кроссворд не найден"));
    }

    @Override
    public void save(Crossword crossword, String crosswordName, String id) {
        crossword.setId((id == null) ? UUID.randomUUID().toString() : id);
        crossword.setName(crosswordName);
        try {
            crosswordDao.save(crossword);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Не удалось сохранить файл: " + e.getMessage());
        }
    }
}
