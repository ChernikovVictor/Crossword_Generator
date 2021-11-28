function LogIn() {
    let body = {
        "login": `${$("#login").val()}`,
        "password": `${$("#password").val()}`
    }

    // let postAuth = $.post("localhost:8080/system/authorize",
    //     JSON.stringify(body),
    //     function (responce) {
    //         alert(responce);
    //     },
    //     "json"
    //     );

    // DO POST
    $.ajax({
        type : "POST",
        contentType : "application/json",
        url : "http://localhost:8080/system/authorize",
        data : JSON.stringify(body),
        dataType : 'json',
        success : function(result) {
            console.log(result);
        },
        error : function(e) {
            alert("Error!")
            console.log("ERROR: ", e);
        }
    });

}