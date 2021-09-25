package com.course.crossword.model.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.course.crossword.model.Constants.ADMIN_ROLE;
import static com.course.crossword.model.Constants.USER_ROLE;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credentials implements Serializable {

    String id;
    String login;
    String password;

    @JsonIgnore
    public String getRole() {
        if (login != null && password != null && login.equals("admin") && password.equals("admin")) {
            return ADMIN_ROLE;
        } else {
            return USER_ROLE;
        }
    }

    @JsonIgnore
    public boolean isInvalid() {
        return (login == null) || (password == null) || (login.length() < 4) || (login.length() > 8)
                || (password.length() < 4) || (password.length() > 10);
    }

}
