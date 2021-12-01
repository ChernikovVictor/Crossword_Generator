start();

function start() {
    $("#formLogIn").css({"display": "block"});
    $("#formRegistration").css({"display": "none"});
}

function logIn() {
    let body = {
        "login": `${$("#login").val()}`,
        "password": `${$("#password").val()}`
    }

    $.ajax({
        type : "POST",
        contentType : "application/json",
        url : "http://localhost:8080/system/authorize",
        data : JSON.stringify(body),
        dataType : 'json',
        success : function(response) {
            callbackAuthorize(response);
        },
        error : function(e) {
            alert("Error!")
            console.log("ERROR: ", e);
        }
    });
}

function callbackAuthorize(response) {
    if (response.result === "SUCCESS") {
        if (response.message === "admin") {
            window.location.href = "adminPage.html";
        }
        else {
            alert("Вы авторизовались под пользователем");
            //window.location.href = "index.html";
        }
    }
    else if (response.result === "ERROR") {
        alert(response.message);
    }
    else {
        alert("Неизвестная ошибка в авторизации. Обратитесь к разработчикам");
    }
}

function registrationInSystem() {
    $("#formLogIn").css({"display": "none"});
    $("#formRegistration").css({"display": "block"});
}

function registration() {
    let login = $("#loginRegistration").val();
    let passFirst = $("#passwordFirstRegistration").val();
    let passSecond = $("#passwordSecondRegistration").val();

    let body = {
        "login": login,
        "password": passFirst
    }

    let errorMsg = "";

    pattern = /^[a-zA-Z](.[a-zA-Z0-9_-]*)$/;
    let validLogin = pattern.test(login);

    patternPasss = /(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z])/;
    let validPass = patternPasss.test(passFirst);

    if (!validLogin || login.length < 6) {
        errorMsg = "Логин не соответствует формату\n";
    }

    if (passFirst.length < 6) {
        errorMsg += "Длина пароля должна быть не менее 6 символов\n";
    }

    if (!validPass) {
        errorMsg += "Пароль не соответствует формату!\n";
        $("#descPass1").css({"color": "red"});
        $("#descPass2").css({"color": "red"});
    }

    if (passFirst !== passSecond || !passFirst || !passSecond) {
        errorMsg += "Пароли не совпадают!\n";
    }

    if (errorMsg) {
        alert(errorMsg);
    }
    else {
        registrtionUser(body);
    }
}

function registrtionUser(body) {
    $.ajax({
        type : "POST",
        contentType : "application/json",
        url : "http://localhost:8080/system/register",
        data : JSON.stringify(body),
        dataType : 'json',
        success : function(response) {
            if (response.result === "SUCCESS") {
                start();
                alert("Вы успешно зарегистрировались");
            }
            else if (response.result === "ERROR") {
                alert(response.message);
            }
            else {
                alert("Неизвестная ошибка в авторизации. Обратитесь к разработчикам");
            }
        },
        error : function(e) {
            alert("Error!")
            console.log("ERROR: ", e);
        }
    });
}