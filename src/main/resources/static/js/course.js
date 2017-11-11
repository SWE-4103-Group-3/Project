$(document).ready(function () {
    var grid;
    var courseID = $('input#courseID').attr('value');

    $.ajax({
        type: "get",
        url: "/courses/" + courseID,
        dataType: "json",
        success: function (data, status) {
            grid = new Grid({
                rows: data.rows,
                cols: data.cols,
                seats: data.seats,
                states: ["open", "closed", "reserved"]
            });
            $('#course-seating-grid').append(grid.el);
        },
        error: function (data, status) {
            console.log(data);
        }
    });

    $('#editsave').on('click', function (e) {
        if (grid.editable) {
            grid.editable = false;
            $('#editsave').html('Edit');
            var seats = grid.getSeats();

            $.ajax({
                type: "post",
                url: "/courses/" + courseID + "/seats",
                data: JSON.stringify(seats),
                contentType: "application/json",
                error: function (data, status) {
                    console.log(data);
                },
                success: function() {
                    toastr.success("Successfully saved seating template!");
                }
            });
        } else {
            grid.editable = true;
            $('#editsave').html('Save');
        }
    });

    //Clear Student Ties to Seats
    $('#editclearseating').on('click', function(e){
        $('#modal-danger-clear-student')
            .css('display', 'block')
            .css('opacity', '1');

        $('#modal-danger-clear-student-close-top')
            .add('#modal-danger-clear-student-close-bottom')
            .add('#modal-danger-clear-student-continue')
            .on('click', function(e){
                $('#modal-danger-clear-student')
                    .css('display', 'none')
                    .css('opacity', '0');
            });

        $('#modal-danger-clear-student-continue').on('click', function(e){
            /* GATED BY https://github.com/SWE-4103-Group-3/Project/issues/43
            var seats = grid.getSeats();
            $.ajax({
                type: "post",
                url: "/courses/" + courseID + "/seats",
                data: JSON.stringify(seats),
                contentType: "application/json",
                error: function (data, status) {
                    console.log(data);
                },
                success: function() {
                    toastr.success("Successfully cleared student seating!");
                }
            });
            */
        });
    });

    $('#editcleartemplate').on('click', function(e){
        $('#modal-danger-reset-template')
            .css('display', 'block')
            .css('opacity', '1');

        $('#modal-danger-reset-template-close-top')
            .add('#modal-danger-reset-template-close-bottom')
            .add('#modal-danger-reset-template-continue')
            .on('click', function(e){
                $('#modal-danger-reset-template')
                    .css('display', 'none')
                    .css('opacity', '0');
            });

        $('#modal-danger-reset-template-continue').on('click', function(e) {
            grid.clearSeats();

            $.ajax({
                type: "post",
                url: "/courses/" + courseID + "/seats",
                data: JSON.stringify(grid.getSeats()),
                contentType: "application/json",
                error: function (data, status) {
                    console.log(data);
                },
                success: function() {
                    toastr.success("Successfully reset seat template!");
                }
            });
        });
    });
});