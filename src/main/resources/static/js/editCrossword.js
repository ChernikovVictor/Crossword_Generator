let crossword;
let wordsRemoveDictionary = [];
let allWordsObjectDictionary = [];
let def = "";
let selectedWords;

/* добавление таблицы в разметку (просто пустые ячейки, для демонстрации) */
const tBody = document.querySelector('tbody');
start();

function start() {
    $.ajax({
        type : "GET",
        contentType : "application/json",
        url : "http://localhost:8080/crosswords/crossword?id=" + localStorage.getItem('instanceCrossword') + "&login=admin",
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

    reColorize();
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
                    if (range[0][0] === range[1][0] && range[0][1] === range[1][1]) {
                        printInfoAbouWord(range[0][1], range[0][0]);
                    }
                    else {
                        let mask = getMasc();
                        getMascWords(mask, range);
                        //editTableFor
                    }
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
                        let color = (td.style.background === 'rgb(248, 224, 76)')
                            ? '#ffffff'
                            : td.style.background;

                        const inColRange = (col >= col1) && (col <= col2);
                        td.style.background = inRowRange && inColRange
                            ? '#ffffff'
                            : td.textContent === ""
                                ? '#4f4f4f'
                                : color;   // установка цвета фона ячейки: при вхождении в диапазон и по X и по Y - голубой цвет (выделение), иначе - никакого цвета (нет выделения)
                    });
                });
            }
    }
};
['mousedown', 'mouseup', 'mousemove', 'mouseleave'].forEach(   // перебор имен (типов событий), их будет обрабатывать одна общая функция
    evtType => tBody.addEventListener(evtType, handler)          // обработка всех событий на их всплытии (делегируем ее tbody)
);

function reColorize() {
    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if ($(this).text() === "" ) {
                $(this).css('background', '#4f4f4f');
            }
            else {
                $(this).css('background', '#ffffff');
            }
        });
    });
}

function printInfoAbouWord(x, y) {
    def = crossword.cells[x][y].definitions[0];
    let firstPosition, lastPosition;
    let flag = true;

    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if ($(this).text() === "" ) {
                $(this).css('background', '#4f4f4f');
            }
            else {
                $(this).css('background', '#ffffff');
            }

            if (crossword.cells[x][y].originalValue !== "" && crossword.cells[x][y].originalValue !== null) {
                if (crossword.cells[row][cell].definitions != null &&
                    crossword.cells[row][cell].definitions.indexOf(def) !== -1) {
                    $(this).css('background', '#f8e04c');

                    if (flag) {
                        firstPosition = [row, cell];
                        flag = false;
                    }

                    lastPosition = [row, cell];
                }
            }
        });
    });

    selectedWords = {
        "firstPosition": firstPosition,
        "lastPosition": lastPosition
    };
}

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

    let h = border.up;
    let w = border.left;
    let length = 0;
    console.log("left = "+border.left+" right = "+border.right)
    if(border.down === border.up){
        length = Math.abs(border.left - border.right)+1;
        if(w>0 && crossword.cells[h][w-1].active
            || w+length<crossword.cells[0].length
            && crossword.cells[h][w+length].active){
            masc = "1";
        }
        let rep1 = 0;
        let rep2 = 0;
        for(let i = w;i < w+length;i++){
            if(!crossword.cells[h][i].active){
                if(h>0 && crossword.cells[h-1][i].active ||
                    h+1<crossword.cells.length && crossword.cells[h+1][i].active){
                    masc = "1";
                }
            }
            if(h>0 && crossword.cells[h-1][i].active){
                rep1++;
            }else{
                rep1 = 0;
            }

            if(h<crossword.cells.length-1 && crossword.cells[h+1][i].active){
                rep2++;
            }else{
                rep2 = 0;
            }
            if(rep1>1 || rep2>1){
                masc = "1";
            }
        }
    }else if(border.left === border.right){
        length = Math.abs(border.up - border.down)+1;
        if(h>0 && crossword.cells[h-1][w].active
           || h+length<crossword.cells.length
           && crossword.cells[h+length][w].active){
            masc = "1";
        }
        let rep1 = 0;
        let rep2 = 0;
        for(let i = h;i < h+length;i++){
            if(!crossword.cells[i][w].active){
                if(w>0 && crossword.cells[i][w-1].active ||
                   w+1<crossword.cells[0].length && crossword.cells[i][w+1].active){
                    masc = "1";
                }
            }
            if(w>0 && crossword.cells[i][w-1].active){
                rep1++;
            }else{
                rep1 = 0;
            }

            if(w<crossword.cells[0].length-1 && crossword.cells[i][w+1].active){
                rep2++;
            }else{
                rep2 = 0;
            }
            if(rep1>1 || rep2>1){
                masc = "1";
            }
        }
    }

    if(Math.abs(border.up - border.down) > 1 && Math.abs(border.left-border.right) > 1){
        masc = "1";
    }


    return masc;
}

function getMascWords(filter) {
    $.ajax({
        type : "GET",
        url : "http://localhost:8080/dictionaries/dictionary?name=" + crossword.dictionaryName + "&sort=alphabet&sortDirection=ASC&filter=" + filter,
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
        if (wordsRemoveDictionary.indexOf(response.data[i].value) === -1) {
            allWordsObjectDictionary.push(response.data[i]);
            $('#selectDictionary').append('<option value="' + response.data[i].value + '">' + response.data[i].value + '</option>\n');
        }
    }
}

function findElement(array, element) {
    for (let i = 0; i < array.length; i++) {
        if (array[i].value === element) {
            return i;
        }
    }

    return -1;
}

function onChangeSelect(valueOption) {
    let border = getBorders();
    let countWord = 0;

    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if ((row <= border.down && row >= border.up) &&
                (cell >= border.left && cell <= border.right)) {
                let index = findElement(allWordsObjectDictionary, valueOption);

                if (index !== -1) {
                    $(this).text(valueOption[countWord]);
                    crossword.cells[row][cell].active = true;
                    crossword.cells[row][cell].value = "";
                    crossword.cells[row][cell].originalValue = valueOption[countWord];

                    if (crossword.cells[row][cell].definitions == null) {
                        crossword.cells[row][cell].definitions = [allWordsObjectDictionary[index].definition];
                    }
                    else {
                        crossword.cells[row][cell].definitions.push(allWordsObjectDictionary[index].definition)
                    }

                    countWord++;
                }
            }

        });
    });

    wordsRemoveDictionary[wordsRemoveDictionary.length] = valueOption;
    $('#selectDictionary').find('option').remove();
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

document.addEventListener('keypress', function(event) {
    let currentWord = "";

    if (event.charCode === 32) {
        $('table tr').each(function(row){
            $(this).find('td').each(function(cell){
                if ((row <= selectedWords.lastPosition[0] && row >= selectedWords.firstPosition[0]) &&
                    (cell >= selectedWords.firstPosition[1] && cell <= selectedWords.lastPosition[1])) {
                    if (crossword.cells[row][cell].definitions.length === 1) {
                        currentWord += $(this).text();

                        $(this).text("");
                        $(this).css('background', '#4f4f4f');

                        crossword.cells[row][cell].active = false;
                        crossword.cells[row][cell].value = null;
                        crossword.cells[row][cell].originalValue = null;
                        crossword.cells[row][cell].definitions = null;
                    }
                    else {
                        let index = crossword.cells[row][cell].definitions.indexOf(def);

                        if (index !== -1) {
                            crossword.cells[row][cell].definitions.splice(index, 1);
                        }
                    }
                }
            });
        });

        let index = wordsRemoveDictionary.indexOf(currentWord);
        wordsRemoveDictionary.splice(index, 1);
    }
});

function onClickSave() {
    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if ($(this).text() === "") {
                crossword.cells[row][cell].active = false;
                crossword.cells[row][cell].value = null;
                crossword.cells[row][cell].originalValue = null;
                crossword.cells[row][cell].definitions = null;
            }
        });
    });

    $.ajax({
        type : "POST",
        contentType : "application/json",
        url : "http://localhost:8080/crosswords/crossword?name=" + crossword.name + "&login=admin&id=" + crossword.id,
        data : JSON.stringify(crossword),
        success : function(response) {
            window.location.href = "adminPage.html";
        },
        error : function(e) {
            alert("error");
            console.log(e);
        }
    });
}

function save() {

}

function onExit() {
    window.location.href = "adminPage.html";
}