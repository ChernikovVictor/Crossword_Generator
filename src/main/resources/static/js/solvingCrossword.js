let crossword;
let coordSelectedCell = [];
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
            alert(e.responseText);
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
            //$(this)[0].id = row + "," + cell;
            /*let text = (crossword.cells[row][cell].value === null)
                ? ""
                : crossword.cells[row][cell].value;

            let read = (crossword.cells[row][cell].active)
                ? ""
                : "readonly";

            $(this).append('<input id="' + row + "-" + cell + '" class="inputCell" value="' + text + '" onclick="onClickCell(this);" ' + read +'></input>');*/
//            $(this).attr('id',row+' '+cell);
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
                    /*$(this).children("input").css({
                        "background": "#4f4f4f"
                    });*/
                }
                else {
                    $(this).css('background', '#ffffff');
                    /*$(this).children("input").css({
                        "background": "#ffffff"
                    });*/
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

/*function onClickCell(cell) {
    let idCell = cell.id.split('-');
    coordSelectedCell.push(Number(idCell[0]));
    coordSelectedCell.push(Number(idCell[1]));

    reColorizeUser();
    //range = getCoords();
    //console.log(cell.id);
    printInfoAbouWord(idCell[0], idCell[1]);
}*/

document.addEventListener('keyup', function(event) {
    document.querySelectorAll('br').forEach((e)=>e.remove());

    /*for (let i = 0; i < crossword.cells.length; i++) {
        for (let j = 0; j < crossword.cells[0].length; j++) {
            if(crossword.cells[i][j].originalValue !== null) {
                if (document.getElementById(i + "-" + j).value.length > 1) {
                    document.getElementById(i + "-" + j).value = document.getElementById(i + "-" + j).value.substr(0, 1);
                }
                if (document.getElementById(i + "-" + j).value.length > 0
                    && (document.getElementById(i + "-" + j).value < 'А' || document.getElementById(i + "-" + j).value > 'Я')
                    && (document.getElementById(i + "-" + j).value < 'а' || document.getElementById(i + "-" + j).value > 'я')) {
                    document.getElementById(i + "-" + j).value = '';
                }

                let symbol = document.getElementById(i + "-" + j).value.toUpperCase();
                document.getElementById(i + "-" + j).value = symbol;
            }else{
                document.getElementById(i + "-" + j).value = '';
            }
        }
    }*/

    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if(crossword.cells[row][cell].originalValue !== null) {
                if ($(this).text().length > 1) {
                    $(this).text($(this).text().substr(0, 1));
                }
                if ($(this).text().length > 0 && ($(this).text() < 'А' || $(this).text() > 'Я')
                    && ($(this).text() < 'а' || $(this).text() > 'я')) {
                    $(this).text('');
                }
            }else{
                $(this).text('');
            }
        });
    });
    /*switch (event.type) {
        case 'keyup':
            reColorizeUser();
            console.log("keyup = "+range[1]+' '+range[0]);

            range = event.path[0].id.split('-');

            range[0] = Number(range[0]);
            range[1] = Number(range[1]);

            if(event.path[0].value.length>0){
                if(crossword.cells[0].length>range[0] && crossword.cells[range[1]][range[0]+1].active){
                    range[0]++;
                }else if(crossword.cells.length>range[1] && crossword.cells[range[1]+1][range[0]].active){
                    range[1]++;
                }
                console.log("res = "+range[0]+' '+range[1]);
                document.getElementById(range[0] + "-" + range[1]).focus();
                printInfoAbouWord(range[0], range[1]);
            }
            break;
    }*/
});

function printInfoAbouWord(x, y) {
    def = crossword.cells[x][y].definitions[0];

    /*if (crossword.cells[x][y].definitions.length > 1) {
        if (crossword.cells[x][y + 1]) {
            def = crossword.cells[x][y + 1].definitions;
        }
        else if (crossword.cells[x][y - 1]) {
            def = crossword.cells[x][y - 1].definitions;
        }
    }
    else {
        def = crossword.cells[x][y].definitions;
    }*/

    $('#textDef').text(def);

    /*for (let i = 0; i < crossword.cells.length; i++) {
        for (let j = 0; j < crossword.cells[0].length; j++) {
            if (crossword.cells[x][y].originalValue !== "" && crossword.cells[x][y].originalValue !== null) {
                if (crossword.cells[i][j].definitions != null &&
                    crossword.cells[i][j].definitions.indexOf(def[0]) !== -1) { //&&
                    //$(this).children("input").css('background') !== 'rgb(97, 245, 48) none repeat scroll 0% 0% / auto padding-box border-box') {

                    document.getElementById(i + "-" + j).style.background = "#f8e04c";
                    //$('#' + x + '-' + y).css('background', '#f8e04c');
                }
            }
        }
    }*/

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
    /*for (let i = 0; i < crossword.cells.length; i++) {
        for (let j = 0; j < crossword.cells[0].length; j++) {
            if (crossword.cells[i][j].active) {
                crossword.cells[i][j].value = $(this).text();
                let color = '#61f530';
                if(crossword.cells[i][j].originalValue !== $(this).children("input").text().toUpperCase()){
                    color = '#f53030';
                }
                document.getElementById(i + "-" + j).style.background = color;
                //$(this).children("input").css('background', color);
            }
        }
    }*/

    $('table tr').each(function(row){
        $(this).find('td').each(function(cell){
            if (crossword.cells[row][cell].active) {
                crossword.cells[row][cell].value = $(this).text();
                let color = '#61f530';
                if(crossword.cells[row][cell].originalValue !== $(this).text().toUpperCase()){
                    color = '#f53030';
                }

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
                try {
                    if (row === range[1] && cell === range[0]) {
                        if(crossword.cells[row][cell].active) {
                            $(this).text(crossword.cells[row][cell].originalValue);

                            $(this).css('background', '#61f530');

                            $('#hints').text(--hints);
                        }
                    }
                }catch (e){
                    console.log(e);
                }
            });
        });
    } else {
        alert("Вы использовали все подсказки!");
    }
    crossword.hints = hints;
}

function saveSolution() {
    /*for (let i = 0; i < crossword.cells.length; i++) {
        for (let j = 0; j < crossword.cells[0].length; j++) {
            if (crossword.cells[i][j].active) {
                crossword.cells[i][j].value = $(this).children("input").text();
            }
        }
    }*/

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
        var regex = /^[a-zа-яA-ZА-Я0-9_]*$/u;
        if (!regex.test(name)) {
            alert("Название содержит запрещенные символы!");
            return;
        }
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "http://localhost:8080/crosswords/list?login=" + localStorage.getItem('login'),
            dataType: 'json',
            success: function (response) {
                let isDuplicateExists = false;
                let isOverwriteNeed = false;
                for (let i = 0; i < response.length; i++) {
                    if (response[i].name === name) {
                        isDuplicateExists = true;
                        const result = confirm('Такой кроссворд уже существует. Хотите перезаписать?');
                        if (result) {
                            isOverwriteNeed = true;
                            saveCrossword(name, crossword.id);
                        }
                        break;
                    }
                }

                if (!isDuplicateExists) {
                    saveCrossword(name, null);
                }
            },
            error: function (e) {
                alert(e.responseText)
                console.log("ERROR: ", e);
            }
        });
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
            alert(e.responseText)
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