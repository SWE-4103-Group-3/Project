var removeModalId = 'remove-modal';
var setModalId = 'set-modal';

var removeButtonId = 'remove-button';
var setButtonId = 'set-button';

var grid;
var courseID = $('input#courseID').val();

$(document).ready(function () {
    $.ajax({
        type: "get",
        url: "/courses/" + courseID,
        dataType: "json",
        success: function (course, status) {
            grid = new Grid({
                rows: course.rows,
                cols: course.cols,
                seats: course.seats,
                id: course.id,
                states: ["open", "closed", "reserved"]
            });
            $('#course-seating-grid').append(grid.el);
            if(user.hasExtendedPrivileges) {
                getAbsences(course.id);
            }
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

    $('#take-attendance-button').on('click', function () {
        grid.startAttendance();
        $(".attendance-buttons").css("visibility", "visible");
    });

    $('#submit-attendance-button').on('click', function () {
        var absences = grid.getAbsences();
        var courseID = grid.getCourseID();
        postAbsences(courseID, absences);
        grid.endAttendance();
        $(".attendance-buttons").css("visibility", "hidden");
    });

    $('#cancel-attendance-button').on('click', function () {
        grid.endAttendance();
        $(".attendance-buttons").css("visibility", "hidden");
    });

    //Clear Student Ties to Seats
    $('#editclearseating').on('click', function(){
        displayClearModal();

        $('#modal-danger-clear-student-close-top')
            .add('#modal-danger-clear-student-close-bottom')
            .add('#modal-danger-clear-student-continue')
            .one('click', function(){displayClearModal(false)});

        $('#modal-danger-clear-student-continue').one('click', function(){
            removeStudents(courseID);
        });
    });

    $('#editcleartemplate').on('click', function(){
        displayResetModal();

        $('#modal-danger-reset-template-close-top')
            .add('#modal-danger-reset-template-close-bottom')
            .add('#modal-danger-reset-template-continue')
            .one('click', function(){displayResetModal(false)});

        $('#modal-danger-reset-template-continue').one('click', function() {
            grid.clearSeats();
            postSeats(courseID, grid.getSeats());
        });
    });

    //Reuse Course Grid
    $('#reuse-grid-button').on('click', function (e) {
        var delayTimer;
        displayCourseSearchModal();

        //Cancel, Close Modal, Restore Previous State
        $('#search-course-grid-modal-close-top')
            .add('#search-course-grid-modal-close-bottom')
            .one('click', function () {
                displayCourseSearchModal(false);

                //Reset Modal Properties
                $('#course-id').val('');
                $('#search-course-field').val('');
                $('.course-list-queried-item').css('background-color', 'initial');
            });

        //Close Modal After Submit
        $('#search-course-grid-modal-submit-bottom').one('click', function() {
            displayCourseSearchModal(false);

            //If no course selected
            var courseGridID = $('#course-id').val();
            if(!courseGridID)
                return;

            if(courseGridID == courseID) {
                $('#course-id').val('');
                toastr.error('Sorry, you can\'t do that :( Choose a different course.');
                return;
            }

            var postBody = {
                "currentCourse": courseID,
                "otherCourse": courseGridID
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
            clearTimeout(delayTimer);
            delayTimer = setTimeout(function() {
                var query = $('#search-course-field').val();
                if(query)
                    queryAndPopulateCourses(query);
            }, 250);
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

function removeStudent(seatID) {
    $.ajax({
        type: "post",
        url: "/seats/" + seatID + "/student/remove",
        success: function() {
            toastr.success("Successfully removed student!");
            setTimeout(function(){
                window.location.reload();
            },1600);
        },
        error: displayErrorNotification
    });
}


function postAbsences(courseID, absences) {
    $.ajax({
        type: "post",
        url: "/courses/" + courseID + "/absences",
        data: JSON.stringify(absences),
        contentType: "application/json",
        success: function() {
            toastr.success("Successfully submitted attendance!");
            setTimeout(function(){
                window.location.reload();
            },1600);
        },
        error: displayErrorNotification
    });
}

function postSeat(info) {
    $.ajax({
        type: "post",
        url: "/seats",
        data: JSON.stringify(info),
        contentType: "application/json",
        success: function() {
            window.location.reload();
        },
        error: displayErrorNotification
    });
}

function getAbsences(courseId) {
    $.ajax({
        type: "get",
        url: "/courses/" + courseId + "/absences/today",
        dataType: "json",
        success: function (absences) {
            grid.setAbsences(absences);
        },
        error: displayErrorNotification
    });
}

function removeStudents() {
    $.ajax({
        type: "post",
        url: "/courses/" + courseID + "/seats/students/remove",
        success: function () {
            grid.removeStudents();
            toastr.success("Successfully removed students!");
        },
        error: displayErrorNotification
    });
}
