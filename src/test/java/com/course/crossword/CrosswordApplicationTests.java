package com.course.crossword;

import com.course.crossword.dto.CrosswordParametersDTO;
import com.course.crossword.service.impl.CrosswordServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrosswordApplicationTests
{

    @Autowired
    CrosswordServiceImpl crosswordService;

    @Test
    void contextLoads()
    {
    }

    @Test
    void generateCrossword()
    {
        crosswordService.createCrossword(new CrosswordParametersDTO("name1", 5, 5, true, "Главный"));
    }

}
