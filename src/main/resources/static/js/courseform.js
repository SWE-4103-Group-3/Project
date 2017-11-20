
$(document).ready(function () {
    var courseId = $('input#courseId').val();
    var username = $('input#username').val();

    $('#deleteButton').click(function() {
        $.ajax({
            type: "post",
            url: "/courses/" + courseId + "/delete",
            contentType: "application/json",
            error: function (data, status) {
                console.error(data);
            },
            success: function (data){
                toastr.success("Course successfully deleted.");
                setTimeout(function(){
                    window.location.replace("/" + username);
                },1600);

            }
        });
    });

    $('#confirmDelete').change(function() {
        $('#deleteButton').attr('disabled',!this.checked);
    });

    $('.datepicker').pickadate({
        formatSubmit: 'yyyy-mm-dd',
        hiddenName: true
    });
    $('.timepicker').pickatime({
        twelvehour: true
    });

});

$(document).ready(function() {
    $('#course-form-collapse-button').on('click', function() {
        $('#courseForm').slideToggle();
    });

    $('#reuse-grid-button').on('click', function (e) {
        displayCourseSearchModal();

        //Cancel, Close Modal, Restore Previous State
        $('#search-course-grid-modal-close-top')
            .add('#search-course-grid-modal-close-bottom')
            .one('click', function () {
                displayCourseSearchModal(false);

                $('#course-grid-id').val('');
                $('#search-course-field').val('');
                $('.course-list-queried-item').css('background-color', 'initial');
            });

        //Close Modal After Submit
        $('#search-course-grid-modal-submit-bottom').one('click', function(e) {
            displayCourseSearchModal(false);

            if($('#course-grid-id').val()) {
                $('#courseCols').val('1');
                $('#courseRows').val('1');
                $('#course-form-rows').hide();
                $('#course-form-cols').hide();
            }
        });

        //Query for Courses Using Value From Input Field
        $('#search-course-field').on('input', function(e) {
            var query = $('#search-course-field').val();
            if(query)
                queryAndPopulateCourses(query);
        });
    });
});

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

        $('#course-grid-id').attr('value', courseID);
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