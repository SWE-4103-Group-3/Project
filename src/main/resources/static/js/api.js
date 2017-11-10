function postSeat(info) {
    $.ajax({
        type: "post",
        url: "/seats",
        data: JSON.stringify(info),
        contentType: "application/json",
        success: function() {
            window.location.reload();
        },
        error: function (data) {
            console.error(data);
        }
    });
}