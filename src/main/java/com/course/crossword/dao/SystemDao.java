package com.course.crossword.dao;

import com.course.crossword.model.system.Credentials;

import java.io.IOException;
import java.util.List;

public interface SystemDao {

    List<Credentials> getAllUsers();

    void save(Credentials credentials) throws IOException;

}
