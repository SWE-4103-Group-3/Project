$(document).ready(function () {
    var grid;
    var courseID = $('input#courseID').val();

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
        error: displayErrorNotification
    });

    $('#editsave').on('click', function () {
        if(!grid.editable) {
            grid.editable = true;
            $(this).html('Save');
            return;
        }

        var seats = grid.getSeats();
        postSeats(courseID, seats);

        grid.editable = false;
        $(this).html('Edit');
    });

    //Clear Student Ties to Seats
    $('#editclearseating').on('click', function(){
        displayClearModal();

        $('#modal-danger-clear-student-close-top')
            .add('#modal-danger-clear-student-close-bottom')
            .add('#modal-danger-clear-student-continue')
            .on('click', function(){displayResetModal(false)});

        $('#modal-danger-clear-student-continue').on('click', function(){
            /* GATED BY https://github.com/SWE-4103-Group-3/Project/issues/43
            var seats = grid.getSeats();
            postSeats(courseID, seats);
            */
        });
    });

    $('#editcleartemplate').on('click', function(){
        displayResetModal();

        $('#modal-danger-reset-template-close-top')
            .add('#modal-danger-reset-template-close-bottom')
            .add('#modal-danger-reset-template-continue')
            .on('click', function(){displayResetModal(false)});

        $('#modal-danger-reset-template-continue').on('click', function() {
            grid.clearSeats();
            postSeats(courseID, grid.getSeats());
        });
    });

    //Reuse Course Grid
    $('#reuse-grid-button').on('click', function (e) {
        displayCourseSearchModal();

        //Cancel, Close Modal, Restore Previous State
        $('#search-course-grid-modal-close-top')
            .add('#search-course-grid-modal-close-bottom')
            .on('click', function () {
                displayCourseSearchModal(false);

                //Reset Modal Properties
                $('#course-grid-id').val('');
                $('#search-course-field').val('');
                $('.course-list-queried-item').css('background-color', 'initial');
            });

        //Close Modal After Submit
        $('#search-course-grid-modal-submit-bottom').on('click', function() {
            displayCourseSearchModal(false);

            var postBody = {
                "currentCourse": courseID,
                "otherCourse": $('#course-grid-id').val()
            };

            //Post and Reload Page on Success
            $.ajax({
                type: "post",
                url: "/course/gridReuse",
                contentType: "application/json",
                data: JSON.stringify(postBody),
                success: function () {
                    window.location.reload(true);
                },
                error: displayErrorNotification
            });
        });

        //Query for Courses Using Value From Input Field
        $('#search-course-field').on('input', function() {
            var query = $('#search-course-field').val();
            if(query)
                queryAndPopulateCourses(query);
        });
    });
});

//Display or Hide Clear Grid Modal
function displayClearModal() {
    var display = (arguments.length === 0 || arguments[0] === true);
    var $modal = $('#modal-danger-clear-student');

    displayModal($modal, display);
}

//Display or Hide Reset Grid Modal
function displayResetModal() {
    var display = (arguments.length === 0 || arguments[0] === true);
    var $modal = $('#modal-danger-reset-template');

    displayModal($modal, display);
}

//Display or Hide Course Search Modal
function displayCourseSearchModal() {
    var display = (arguments.length === 0 || arguments[0] === true);
    var $modal = $('#search-course-grid-modal');

    displayModal($modal, display);
}

//Display or Hide Generic Modal
function displayModal($modal, display) {
    if(display) {
        $modal.css('display', 'block').css('opacity', '1');
    } else {
        $modal.css('display', 'none').css('opacity', '0');
    }
}

//Post Updated Seats for Course
function postSeats(courseID, seats) {
    $.ajax({
        type: "post",
        url: "/courses/" + courseID + "/seats",
        data: JSON.stringify(seats),
        contentType: "application/json",
        success: function() {
            toastr.success("Successfully saved seating template!");
        },
        error: displayErrorNotification
    });
}

//Query Backend for Courses Matching Query String, Populate Queried Course List
function queryAndPopulateCourses(query) {
    var courseLineEventHandler = function() {
        $('.course-list-queried-item').css('background-color', 'initial');
        $(this).css('background-color', 'lightgray');

        //Extract Course ID From Class
        //Split classes into array of strings, find class beginning with `cid-` then extract id number.
        var courseID = "";
        var classes = $(this).attr('class').split(/\s+/g);
        for(var classIndex = 0; classIndex < classes.length; classIndex++)
            if(classes[classIndex].match(/cid-/g))
                courseID = classes[classIndex].split(/cid-/g)[1];

        $('#course-grid-id').val(courseID);
    };

    $.ajax({
        type: "get",
        url: "/courses/query/" + query,
        dataType: "json",
        success: function (data, status) {
            //Remove all previously queried courses
            var courseList = $('#course-list-queried').empty();

            //For each course, create div element with relevant course information
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
        error: displayErrorNotification
    });
}

//Display Error Notification
function displayErrorNotification() {
    toastr.error('Something went wrong :( Try reloading the page or retry later.');
}