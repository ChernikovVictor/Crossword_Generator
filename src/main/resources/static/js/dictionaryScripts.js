// const dictionaryName = 'dict1';
const dictionaryName = localStorage.getItem("dictionaryName");
const baseUrl = "http://localhost:8080/dictionaries/dictionary?name=" + dictionaryName;

window.onload = function () {
    document.getElementById("dictionaryNameParagraph").innerHTML = "Словарь: " + dictionaryName;
    document.getElementById("addWordButton").onclick = function () {
        document.getElementById("addWordForm").style.display = "block";
    }
    document.getElementById("hideAddPopUp").onclick = function () {
        document.getElementById("addWordForm").style.display = "none";
        document.getElementById("addWordInputValue").value = "";
        document.getElementById("addWordInputDefinition").value = "";
    }
    document.getElementById("hideEditPopUp").onclick = function () {
        document.getElementById("editWordForm").style.display = "none";
    }
    document.getElementById("addWordFormSubmit").onclick = function () {
        var value = $("#addWordInputValue").val();
        if (!value) {
            alert("Понятие не может быть пустым!");
            return;
        }
        var definition = $("#addWordInputDefinition").val();
        if (!definition) {
            alert("Заполните определение для понятия!");
            return;
        }
        var body = {
            value: value,
            definition: definition
        };
        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : baseUrl,
            data : JSON.stringify(body),
            success : function(result) {
                document.getElementById("hideAddPopUp").click();
                getWords();
            },
            error : function(e) {
                alert(e.responseText);
                console.log("ERROR: ", e);
            }
        });
    }
    document.getElementById("editWordFormSubmit").onclick = function () {
        var value = $("#editWordInputValue").val();
        var oldValue = $("#oldEditWordInputValue").val();
        var definition = $("#editWordInputDefinition").val();
        var body = {
            value: value,
            definition: definition
        };
        $.ajax({
            type : "PUT",
            contentType : "application/json",
            url : baseUrl + "&oldValue=" + oldValue,
            data : JSON.stringify(body),
            success : function(result) {
                document.getElementById("hideEditPopUp").click();
                getWords();
            },
            error : function(e) {
                alert(e.responseText);
                console.log("ERROR: ", e);
            }
        });
    }
}

function getFilterForRequest() {
    var mask = $("#findByMaskInput").val();
    return "&filter=" + mask.replaceAll("?", "1");
}

function getSortForRequest() {
    var sortMode = $("#sortMode").val();
    if (sortMode === "Длине, возр.") {
        return "&sort=length&sortDirection=ASC";
    } else if (sortMode === "Длине, убыв.") {
        return "&sort=length&sortDirection=DESC";
    } else if (sortMode === "Алфавиту, возр.") {
        return "&sort=alphabet&sortDirection=ASC";
    } else {
        return "&sort=alphabet&sortDirection=DESC";
    }
}

function getWords() {
    var filter = getFilterForRequest();
    var sort = getSortForRequest();
    var url = baseUrl + filter + sort;
    $.ajax({
        type: "GET",
        url: url,
        success: function (result) {
            console.log(result);
            createDictionaryTable(result);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

var words;

function createDictionaryTable(response) {
    words = response.data;
    var html = '<tr><th>Понятие</th><th>Определение</th></tr>';
    for (var i = 0; i < words.length; i++) {
        var word = words[i];
        var valueTd = '<td class="value">' + words[i].value + '</td>';
        var definitionTd = '<td class="definition">' + words[i].definition + '</td>';
        var editButtonTd = '<td class="icon"><button class="editButton" onclick="editWord(this.id);" id="' + words[i].value + '"/><i class="fa fa-edit"></i></td>';
        var deleteButtonTd = '<td class="icon"><button class="deleteButton" onclick="deleteWord(this.id);" id="' + words[i].value + '"/><i class="fa fa-trash"></i></td>';
        var tableRow = '<tr>' + valueTd + definitionTd + editButtonTd + deleteButtonTd + '</tr>';
        html += tableRow;
    }
    document.getElementById("wordsTable").innerHTML = html;
}

function editWord(id) {
    for (var i = 0; i < words.length; i++) {
        if (words[i].value === id) {
            document.getElementById("editWordInputValue").value = id;
            document.getElementById("oldEditWordInputValue").value = id;
            document.getElementById("editWordInputDefinition").value = words[i].definition;
        }
    }
    document.getElementById("editWordForm").style.display = "block";
}

function deleteWord(id) {
    $.ajax({
        type : "DELETE",
        contentType : "application/json",
        url : baseUrl + "&value=" + id,
        data : JSON.stringify(""),
        success : function(result) {
            getWords();
        },
        error : function(e) {
            alert(e.responseText);
            console.log("ERROR: ", e);
        }
    });
}

function exit() {
    window.location.href = "adminPage.html";
}


