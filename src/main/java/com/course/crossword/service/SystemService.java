package com.course.crossword.service;

import com.course.crossword.dto.CredentialsResponse;

public interface SystemService {

    CredentialsResponse register(String login, String password);

    CredentialsResponse authorize(String login, String password);
}
