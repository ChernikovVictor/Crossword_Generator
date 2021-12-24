let crossword;
/* добавление таблицы в разметку (просто пустые ячейки, для демонстрации) */
const tBody = document.querySelector('tbody');

start();

function start() {
    let original = localStorage.getItem('crosswordIsOriginal');
    let url = "http://localhost:8080/crosswords/crossword?id=" + localStorage.getItem('crosswordId') + "&login=";

    if (original === "true") {
        url += "admin";
    }
    else {
        url += localStorage.getItem('login');
    }

    $.ajax({
        type : "GET",
        contentType : "application/json",
        url : url,
        dataType : 'json',
        success : function(response) {
            crossword = response;
            createUserTable(response.cells);
            $('#hints').text(crossword.hints);
        },
        error : function(e) {
            alert("error");
            console.log(e);
        }
    });
}

function createUserTable(data) {
    let width = 0;
    let height = 0;

    width = data[0].length;
    height = data.length;

    for (let tr, i = 0; i < width * height; i++) {
        if (!(i % width))
            tr = tBody.insertRow();
        let td = tr.insertCell();
    }


    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            $(this).text(crossword.cells[row][cell].value);
        });
    });


    reColorizeUser();
}

function reColorizeUser() {
    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if ($(this).css('background') !== 'rgb(97, 245, 48) none repeat scroll 0% 0% / auto padding-box border-box') {
                if (crossword.cells[row][cell].originalValue === null) {
                    $(this).css('background', '#4f4f4f');
                }
                else {
                    $(this).css('background', '#ffffff');
                }
            }
        });
    });
}

let range = [-1, -1];
const handler = evt => {
    const tr = evt.target.closest('tr');
    const getCoords = () => [                   // функция получения табличных координат мыши
        [...tr.cells].indexOf(evt.target),        // индекс ячейки в строке (X-координата)
        [...tr.parentElement.rows].indexOf(tr),   // индекс строки в таблице (Y-координата)
    ];
    switch (evt.type) {
        case 'mousedown':
            reColorizeUser();
            range = getCoords();
            printInfoAbouWord(range[1], range[0]);
            break;
    }
};

tBody.addEventListener('mousedown', handler);

document.addEventListener('keyup', function(event) {
    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if(crossword.cells[row][cell].originalValue !== null) {
                if ($(this).text().length > 1) {
                    $(this).text($(this).text().substr(0, 1));
                }
                if ($(this).text().length > 0 && ($(this).text() < 'А' || $(this).text() > 'Я')) {
                    $(this).text('');
                }
                if ($(this).text().length > 0 && $(this).text() !== $(this).text().toUpperCase()) {
                    $(this).text($(this).text().toUpperCase());
                }
            }else{
                $(this).text('');
            }
        });
    });
});

function printInfoAbouWord(x, y) {
    def = crossword.cells[x][y].definitions[0];
    $('#textDef').text(def);

    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if (crossword.cells[x][y].originalValue !== "" && crossword.cells[x][y].originalValue !== null) {
                if (crossword.cells[row][cell].definitions != null &&
                    crossword.cells[row][cell].definitions.indexOf(def) !== -1 &&
                    $(this).css('background') !== 'rgb(97, 245, 48) none repeat scroll 0% 0% / auto padding-box border-box') {

                    $(this).css('background', '#f8e04c');
                }
            }
        });
    });
}

function checkCrossword() {
    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if (crossword.cells[row][cell].active) {
                crossword.cells[row][cell].value = $(this).text();

                let color = (crossword.cells[row][cell].originalValue !== $(this).text())
                    ? '#f53030'
                    : '#61f530';

                $(this).css('background', color);
            }
        });
    });
}

function getHints() {
    let hints = $('#hints').text();

    if (hints > 0) {
        $('table tr').each(function(row){
            $(this).find('td').each(function(cell){
                if (row === range[1] && cell === range[0]) {
                    $(this).text(crossword.cells[row][cell].originalValue);

                    $(this).css('background', '#61f530');

                    $('#hints').text(--hints);
                }

            });
        });
    } else {
        alert("Вы использовали все подсказки!");
    }
}

function saveSolution() {
    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if (crossword.cells[row][cell].active) {
                crossword.cells[row][cell].value = $(this).text();
            }
        });
    });

    validateNameCrossword();
}

function validateNameCrossword() {
    const name = prompt('Укажите, пожалуйста, наименование кроссворда');
    if (name) {
        $.ajax({
            type : "GET",
            contentType : "application/json",
            url : "http://localhost:8080/crosswords/list?login=" + localStorage.getItem('login'),
            dataType : 'json',
            success : function(response) {
                let flag = false;

                for (let i = 0; i < response.length; i++) {
                    if (response[i].name === name) {
                        const result = confirm('Такой кроссворд уже существует. Хотите перезаписать?');
                        if (result) {
                            flag = true;
                            saveCrossword(name, crossword.id);
                        }
                        else {
                            validateNameCrossword();
                        }
                    }
                }

                if (!flag) {
                    saveCrossword(name, null);
                }
            },
            error : function(e) {
                alert("Error!")
                console.log("ERROR: ", e);
            }
        });
    } else {
        validateNameCrossword();
    }
}

function saveCrossword(name, id) {
    let url = "http://localhost:8080/crosswords/crossword?name=" + name +"&login=" + localStorage.getItem('login');

    if (id !== null) {
        url += "&id=" + id;
    }

    $.ajax({
        type : "POST",
        contentType : "application/json",
        url : url,
        data : JSON.stringify(crossword),
        success : function(response) {
            window.location.href = "userPage.html";
        },
        error : function(e) {
            alert("Error!")
            console.log("ERROR: ", e);
        }
    });
}

function out() {
    window.location.href = "userPage.html";
}

function aboutSystem() {
    window.open('http://localhost:8080/aboutSystem.html', '_blank');
}