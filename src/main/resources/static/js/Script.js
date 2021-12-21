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
            window.location.href = "userPage.html";
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

    if (!validLogin || login.length < 6) {
        errorMsg = "Логин не соответствует формату\n";
    }

    if (passFirst.length < 6) {
        errorMsg += "Длина пароля должна быть не менее 6 символов\n";
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

function outAdmin() {
    window.location.href = "index.html";
}

function aboutSystem(){
    window.open('http://localhost:8080/aboutSystem.html', '_blank');
}
function aboutDevelopers(){
    window.location.href = "aboutDevelopers.html";
}
function createCrossword() {
    window.location.href = "createCrossword.html";
}

function playCrossword(){
    window.location.href = "";
}

function editCrossword(selection) {
    localStorage.setItem("instanceCrossword", selection);
    window.location.href = "crosswordAdmin.html";
}

function chooseCrossword() {
    $.ajax({
        type : "GET",
        url : "http://localhost:8080/crosswords/list?login=admin ",
        dataType : 'json',
        success : function(response) {
            $('#crosswords').append('<option disabled selected value></option>')
            for (let i = 0; i < response.length; i++) {
                $('#crosswords').append('<option value="' + response[i].id + '">' + response[i].name + '</option>')
            }
        },
        error : function(e) {
            alert(e.responseText)
            console.log("ERROR: ", e);
        }
    });
    document.getElementById("chooseCrosswordForm").style.display = "block";
}

function editDictionary(selection) {
    let dictionaryName = selection[selection.selectedIndex].value;
    localStorage.setItem("dictionaryName", dictionaryName);
    window.location.href = "dictionary.html";
}

function chooseDictionary() {
    $.ajax({
        type : "GET",
        url : "http://localhost:8080/dictionaries/list",
        dataType : 'json',
        success : function(response) {
            $('#dictionaries').append('<option disabled selected value></option>')
            for (let i = 0; i < response.length; i++) {
                $('#dictionaries').append('<option>' + response[i] + '</option>')
            }
        },
        error : function(e) {
            alert(e.responseText)
            console.log("ERROR: ", e);
        }
    });
    document.getElementById("chooseDictionaryForm").style.display = "block";
}

function closePopUpById(id) {
    document.getElementById(id).style.display = "none";
    $('#dictionaries').find('option').remove();
    document.getElementById("createDictionaryInput").value = "";
    document.getElementById("file").value = "";
}

function createDictionary() {
    var dictionaryName = $("#createDictionaryInput").val();
    $.ajax({
        type : "GET",
        url : "http://localhost:8080/dictionaries/list",
        dataType : 'json',
        success : function(response) {
            var isDuplicate = false;
            for (let i = 0; i < response.length; i++) {
                if (response[i] === dictionaryName) {
                    isDuplicate = true;
                    break;
                }
            }
            if (isDuplicate) {
                alert("Словарь с таким названием уже существует");
            } else {
                $.ajax({
                    type: "PUT",
                    contentType: "application/json",
                    url: 'http://localhost:8080/admin/dictionary?name=' + dictionaryName,
                    success: function (result) {
                        closePopUpById('createDictionaryForm');
                        alert("Словарь создан успешно");
                    },
                    error: function (e) {
                        alert("Произошла ошибка на сервере: " + e.responseText);
                        console.log("ERROR: ", e);
                    }
                });
            }
        },
        error : function(e) {
            alert(e.responseText)
            console.log("ERROR: ", e);
        }
    });
}

function importDictionary() {
    var formElement = document.querySelector("#importForm");
    var request = new XMLHttpRequest();
    request.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            closePopUpById("importDictionaryForm");
            alert("Словарь успешно импортирован");
        } else if (this.readyState === 4 && this.status !== 200) {
            alert(this.responseText);
        }
    }
    request.open("POST", "http://localhost:8080/admin/dictionary");
    request.send(new FormData(formElement));
}