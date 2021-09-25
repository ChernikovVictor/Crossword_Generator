package com.course.crossword.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class JsonLoader<T> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private final Class<T> type;

    public JsonLoader(Class<T> type) {
        this.type = type;
    }

    public void saveAsJson(T object, String filepath) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filepath)) {
            MAPPER.writeValue(fileWriter, object);
            log.info("Данные успешно сохранены в {}", filepath);
        }
    }

    public T loadFromJson(String filepath) throws IOException {
        try (FileReader fileReader = new FileReader(filepath)) {
            T result = MAPPER.readValue(fileReader, type);
            log.info("Данные успешно считаны из {}", filepath);
            return result;
        }
    }
}
