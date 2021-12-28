let listCrossword;

start();

function start() {
    loadDictionary();
}

function loadDictionary() {
    $.ajax({
        type : "GET",
        url : "http://localhost:8080/dictionaries/list",
        dataType : 'json',
        success : function(response) {
            $('#dictionaryList').append('<option disabled selected value></option>');
            for (let i = 0; i < response.length; i++) {
                $('#dictionaryList').append('<option>' + response[i] + '</option>')
            }
        },
        error : function(e) {
            alert(e.responseText)
            console.log("ERROR: ", e);
        }
    });

    $.ajax({
        type : "GET",
        url : "http://localhost:8080/crosswords/list?login=admin",
        dataType : 'json',
        success : function(response) {
            listCrossword = response;
        },
        error : function(e) {
            alert(e.responseText)
            console.log("ERROR: ", e);
        }
    });
}

function createCrossword() {
    let errorString = "";

    let body = {
        "name": $("#nameCrossword").val(),
        "height": $("#heightCrossword").val(),
        "width": $("#widthCrossword").val(),
        "auto": $('#autoGenerateCrossword').is(':checked'),
        "dictionary": $("#dictionaryList").val()
    }

    if (!body.name) {
        errorString += "Название кроссворда не может быть пустым!\n";
    }

    if (body.width > 30 || body.width < 5) {
        errorString += "Ширина должна быть не менее 5 и не более 30!\n";
    }

    if (body.height > 30 || body.height < 5) {
        errorString += "Высота должна быть не менее 5 и не более 30!\n";
    }

    if (!body.dictionary) {
        errorString += "Выберите словарь понятий!\n";
    }

    for (let i = 0; i < listCrossword.length; i++) {
        if (listCrossword[i].name === body.name) {
            errorString += "Такое название кроссворда уже существует!\n";
        }
    }

    if (errorString !== "") {
        alert(errorString);
    }
    else {
        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : "http://localhost:8080/admin/crossword",
            data : JSON.stringify(body),
            dataType : 'json',
            success : function(response) {
                alert("Create success => " + response);
            },
            error : function(e) {
                localStorage.setItem('instanceCrossword', e.responseText);
                window.location.href = "crosswordAdmin.html";
            }
        });
    }
}