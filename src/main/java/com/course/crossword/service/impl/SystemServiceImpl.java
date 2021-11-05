package com.course.crossword.service.impl;

import com.course.crossword.dao.SystemDao;
import com.course.crossword.dto.CredentialsResponse;
import com.course.crossword.model.system.Credentials;
import com.course.crossword.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.course.crossword.model.Constants.*;

@Slf4j
@Service
public class SystemServiceImpl implements SystemService {

    private SystemDao systemDao;

    @Autowired
    public SystemServiceImpl(SystemDao systemDao) {
        this.systemDao = systemDao;
    }

    @Override
    public CredentialsResponse register(String login, String password) {
        Credentials credentials = new Credentials(UUID.randomUUID().toString(), login, password);
        if (credentials.isInvalid()) {
            return new CredentialsResponse(ERROR, "Некорректные данные", Strings.EMPTY);
        }
        if (isCredentialAlreadyExists(credentials)) {
            return new CredentialsResponse(ERROR, "Пользователь с таким логином уже существует", Strings.EMPTY);
        }
        try {
            systemDao.save(credentials);
            return new CredentialsResponse(SUCCESS, credentials.getRole(), credentials.getId());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return new CredentialsResponse(ERROR, "Не удалось сохранить данные", Strings.EMPTY);
        }
    }

    @Override
    public CredentialsResponse authorize(String login, String password) {
        List<Credentials> credentials = systemDao.getAllUsers();
        Optional<Credentials> cred = credentials.stream()
                .filter(c -> c.getLogin().equals(login))
                .filter(c -> c.getPassword().equals(password))
                .findFirst();
        return cred.map(value -> new CredentialsResponse(SUCCESS, value.getRole(), value.getId()))
                .orElse(new CredentialsResponse(ERROR, "Неверные логин или пароль", Strings.EMPTY));
    }

    private boolean isCredentialAlreadyExists(Credentials cred) {
        List<Credentials> credentials = systemDao.getAllUsers();
        return credentials.stream().anyMatch(c -> c.getLogin().equals(cred.getLogin()));
    }
}
