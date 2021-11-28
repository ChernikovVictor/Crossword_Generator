// DO GET
function ajaxGet() {
    $.ajax({
        type: "GET",
        url: "http//...",
        success: function (result) {
            console.log(result);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

// DO POST
function ajaxPost() {
	var body = {
		attribute: 'value'
	}
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "http://...",
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