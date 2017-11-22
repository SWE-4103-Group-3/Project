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

        $('#course-id').attr('value', courseID);
    };

    $.ajax({
        type: "get",
        url: "/courses/query/" + query,
        dataType: "json",
        success: function (data, status) {
            //Remove all previously queried courses
            var courseList = $('#course-list-queried').empty();

            if(data.length === 0) {
                var $el = $('<div>')
                    .addClass('list-group-item')
                    .addClass('list-group-item-action')
                    .addClass('course-list-queried-item')
                    .text('No courses found :-(');

                courseList.append($el);
                return;
            }
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