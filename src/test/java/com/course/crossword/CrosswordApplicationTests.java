package com.course.crossword;

import com.course.crossword.dto.CrosswordParametersDTO;
import com.course.crossword.service.impl.CrosswordServiceImpl;
import com.course.crossword.service.impl.DictionaryServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrosswordApplicationTests {

    @Autowired
    CrosswordServiceImpl crosswordService;

    @Test
    void contextLoads() {
    }

    @Test
    void generateCrossword() {
        for (int i = 5; i < 31; i++) {
            for (int j = 5; j < 31; j++) {
                crosswordService.createCrossword(new CrosswordParametersDTO("name1", i, j, true, "Главный"));
            }
        }
    }

}
