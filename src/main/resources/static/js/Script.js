function LogIn() {
    let body = {
        "login": `${$("#login").val()}`,
        "password": `${$("#password").val()}`
    }

    let postAuth = $.post("localhost:8080/system/authorize",
        JSON.stringify(body),
        function (responce) {
            alert(responce);
        },
        "json"
        );

}