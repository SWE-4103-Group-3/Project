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

    //Reuse Course Grid Logic
    $('#reuse-grid-button').on('click', function (e) {
        //Display Course Search Modal
        $('#search-course-grid-modal')
            .css('display', 'block')
            .css('opacity', '1');

        //Cancel, Close Modal, Restore Previous State
        $('#search-course-grid-modal-close-top')
            .add('#search-course-grid-modal-close-bottom')
            .on('click', function (e) {
                $('#search-course-grid-modal')
                    .css('display', 'none')
                    .css('opacity', '0');

                $('#course-grid-id').attr('value', '');
                $('#search-course-field').val('');
                $('.course-list-queried-item').css('background-color', 'initial');
            });

        //Close Modal After Submit
        $('#search-course-grid-modal-submit-bottom').on('click', function(e) {
            $('#search-course-grid-modal')
                .css('display', 'none')
                .css('opacity', '0');

            var postBody = {
                "currentCourse": $('#courseID').val(),
                "otherCourse": $('#course-grid-id').val()
            };

            $.ajax({
                type: "post",
                url: "/course/gridReuse",
                contentType: "application/json",
                data: JSON.stringify(postBody),
                success: function () {
                    window.location.reload(true);
                },
                error: function (data, status) {
                    toastr.error('Something went wrong :( Try reloading the page or retry later.');
                }
            });
        });

        //Event Handler for Course Line
        var courseLineEventHandler = function(e) {
            $('.course-list-queried-item').css('background-color', 'initial');
            $(this).css('background-color', 'lightgray');

            var courseID = "";
            var classes = $(this).attr('class').split(/\s+/g);
            for(var classIndex = 0; classIndex < classes.length; classIndex++)
                if(classes[classIndex].match(/cid-/g))
                    courseID = classes[classIndex].split(/cid-/g)[1];

            $('#course-grid-id').attr('value', courseID);
        };

        //Query for Courses Using Value From Input Field
        $('#search-course-field').on('input', function(e) {
            var query = $('#search-course-field').val();
            if(!query)
                return;

            $.ajax({
                type: "get",
                url: "/courses/query/" + query,
                dataType: "json",
                success: function (data, status) {
                    var courseList = $('#course-list-queried').empty();
                    for(var index = 0; index < data.length; index++) {
                        var $el = $('<div>')
                            .attr('id', 'cq-' + data[index].name)
                            .addClass('list-group-item')
                            .addClass('list-group-item-action')
                            .addClass('course-list-queried-item')
                            .addClass('cid-' + data[index].id)
                            .text('Course: ' + data[index].name + ', Section: ' + data[index].section);

                        $el.on('click', courseLineEventHandler);
                        courseList.append($el);
                    }
                },
                error: function (data, status) {
                    toastr.error('Something went wrong :( Try reloading the page or retry later.');
                }
            });
        });
    });
});