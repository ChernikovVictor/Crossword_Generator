class Cell {
    constructor(x, y, active, value, originalValue, definitions) {
        this.x = x;
        this.y = y;
        this.active = active;
        this.value = value;
        this.originalValue = originalValue;
        this.definitions = definitions;
    }
}

let modelsCell;
let crossword;

/* добавление таблицы в разметку (просто пустые ячейки, для демонстрации) */
const tBody = document.querySelector('tbody');
start();

function start() {
    $.ajax({
        type : "GET",
        contentType : "application/json",
        url : "http://localhost:8080/crosswords/crossword?id=" + localStorage.getItem('instanceCrossword') + "&login=admin ",
        dataType : 'json',
        success : function(response) {
            crossword = response;
            createTable(response.cells);
        },
        error : function(e) {
            alert("Error!")
            console.log("ERROR: ", e);
            //localStorage.setItem('instanceCrossword', e.responseText);
            //window.location.href = "crosswordAdmin.html";
        }
    });
}

function createTable(data) {
    let width = 0;
    let height = 0;

    width = data[0].length;
    height = data.length;

    for (let tr, i = 0; i < width * height; i++) {
        if (!(i % width))
            tr = tBody.insertRow();
        tr.insertCell();
    }

    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            $(this).text(crossword.cells[row][cell].originalValue);
        });
    });
}


/* переменные, и функция сортировки (для использования в обработчике) */
const mathSort = (a, b) => a - b;
let flag = true;
//let mask = "";

let isMbPressed = false,                      // флаг "кнопка мыши зажата"
    range = [[-1, -1], [-1, -1]];             // координаты "углов" выделенного диапазона ячеек, две точки ([столбец, строка])
/* функция обработчика DOM-событий - в ней основная логика */
const handler = evt => {
    const tr = evt.target.closest('tr');
    const getCoords = () => [                   // функция получения табличных координат мыши
        [...tr.cells].indexOf(evt.target),        // индекс ячейки в строке (X-координата)
        [...tr.parentElement.rows].indexOf(tr),   // индекс строки в таблице (Y-координата)
    ];
    switch (evt.type) {                         // ветвление по типу события
        case 'mouseleave':
            if (evt.target === evt.currentTarget)
                isMbPressed = false;                  // при выходе курсора за границы tbody, снимаем флаг зажатия
            break;
        case 'mousedown':
        case 'mouseup':
            isMbPressed = evt.type === 'mousedown';
            range[+!isMbPressed] = getCoords();     // если событие зажатия, запомним как координаты начальной точки (range[0]), иначе как координаты конечной точки (range[1])
        case 'mousemove':
            if (!isMbPressed) {
                if (!flag) {
                    flag = true;
                    let mask = getMasc();
                    getMascWords(mask, range);
                    //editTableFor
                }
                break;                // движения мыши без зажатой кнопки не обрабатываем
            }

            flag = false;

            range[1] = getCoords();                 // обновляем координату конечной точки диапазона
            //console.log(JSON.stringify(range));

            if (range[0][1] === range[1][1] || range[0][0] === range[1][0]) {
                const [col1, col2] = [range[0][0], range[1][0]].sort(mathSort),     // возьмем X-координаты по возрастанию их значений
                    [row1, row2] = [range[0][1], range[1][1]].sort(mathSort);     // и Y-координаты аналогично
                [...tBody.rows].forEach((tr, row) => {
                    const inRowRange = (row >= row1) && (row <= row2);
                    [...tr.cells].forEach((td, col) => {
                        const inColRange = (col >= col1) && (col <= col2);
                        td.style.background = inRowRange && inColRange ? '#556c81' : '';   // установка цвета фона ячейки: при вхождении в диапазон и по X и по Y - голубой цвет (выделение), иначе - никакого цвета (нет выделения)
                    });
                });
            }
    }
};
['mousedown', 'mouseup', 'mousemove', 'mouseleave'].forEach(   // перебор имен (типов событий), их будет обрабатывать одна общая функция
    evtType => tBody.addEventListener(evtType, handler)          // обработка всех событий на их всплытии (делегируем ее tbody)
);

function getMasc() {
    let border = getBorders();
    let masc = "";

    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if ((row <= border.down && row >= border.up) &&
                (cell >= border.left && cell <= border.right)) {
                masc += $(this).text() === ""
                    ? "1"
                    : $(this).text();
            }
        });
    });

    return masc;
}

function getMascWords(filter) {
    $.ajax({
        type : "GET",
        /*url : "http://localhost:8080/dictionaries/dictionary?name=" +
            + crossword.dictionaryName + "&sort=alphabet&sortDirection=ASC&filter=" + filter,*/
        url : "http://localhost:8080/dictionaries/dictionary?name=Главный&sort=alphabet&sortDirection=ASC&filter=" + filter,
        dataType : 'json',
        success : function(response) {
            processingResponse(response);
            //console.log(response.data[0].value);
            //flag = false;
        },
        error : function(e) {
            alert("Error!")
            console.log("ERROR: ", e);
        }
    });
}

function processingResponse(response) {
    $('#selectDictionary').find('option').remove();

    for (let i = 0; i < response.data.length; i++) {
        $('#selectDictionary').append('<option value="' + response.data[i].value + '">' + response.data[i].value + '</option>\n');
    }
}

function onChangeSelect(valueOption) {
    let border = getBorders();
    let countWord = 0;

    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if ((row <= border.down && row >= border.up) &&
                (cell >= border.left && cell <= border.right)) {
                $(this).text(valueOption[countWord]);
                countWord++;
            }
        });
    });
}

function getBorders() {
    let border = {
        "left": 0,
        "right": 0,
        "down": 0,
        "up": 0
    };

    if (range[0][0] === range[1][0]) {
        border.left = range[0][0];
        border.right = range[0][0];
    }
    if (range[0][0] > range[1][0]) {
        border.left = range[1][0];
        border.right = range[0][0];
    }
    if (range[0][0] < range[1][0]) {
        border.left = range[0][0];
        border.right = range[1][0];
    }

    if (range[0][1] === range[1][1]) {
        border.up = range[0][1];
        border.down = range[0][1];
    }
    if (range[0][1] > range[1][1]) {
        border.up = range[1][1];
        border.down = range[0][1];
    }
    if (range[0][1] < range[1][1]) {
        border.up = range[0][1];
        border.down = range[1][1];
    }

    return border;
}

function onSave() {
    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if ($(this).text() === "") {
                crossword.cells[row][cell].active = false;
                crossword.cells[row][cell].value = "";
                crossword.cells[row][cell].originalValue = "";
                crossword.cells[row][cell].definitions = [];
            }
            else {
                crossword.cells[row][cell].active = true;
                crossword.cells[row][cell].value = "";
                crossword.cells[row][cell].originalValue = $(this).text();
                crossword.cells[row][cell].definitions = [];
            }
        });
    });

    $.ajax({
        type : "POST",
        contentType : "application/json",
        /*url : "http://localhost:8080/crosswords/crossword?name=" +
            + crossword.name + "&login=admin&id=" +
            + crossword.id,*/
        //url : "http://localhost:8080/crosswords/crossword?name=exsample&login=admin&id=" + crossword.id,
        url: "http://localhost:8080/crosswords/crossword?name=" + crossword.name + "&login=admin",
        data : JSON.stringify(crossword),
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