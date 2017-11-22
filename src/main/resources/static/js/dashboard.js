$("#createButton").click(function (){
    $("#courseForm").slideToggle();
});

$('#searchButton').on('click', function (e) {
    var delayTimer;
    displayCourseSearchModal();

    //Cancel, Close Modal, Restore Previous State
    $('#search-course-grid-modal-close-top')
        .add('#search-course-grid-modal-close-bottom')
        .one('click', function () {
            displayCourseSearchModal(false);

            $('#course-id').val('');
            $('#search-course-field').val('');
            $('.course-list-queried-item').css('background-color', 'initial');
        });

    //Close Modal After Submit
    $('#search-course-grid-modal-submit-bottom').one('click', function(e) {
        displayCourseSearchModal(false);

        if($('#course-id').val()) {
            $.ajax({
                type: 'get',
                url: '/courses/' + $('#course-id').val(),
                dataType: 'json',
                success: function (data, status) {
                    var instructor = data.instructor.username;
                    var courseName = data.name;
                    var courseSection = data.section;
                    var url = '/' + instructor + '/' + courseName + (courseSection ? '/' + courseSection : '');
                    window.location.replace(url);
                },
                error: displayErrorNotification
            });
        }
    });

    //Query for Courses Using Value From Input Field
    $('#search-course-field').on('input', function(e) {
        clearTimeout(delayTimer);
        delayTimer = setTimeout(function() {
            var query = $('#search-course-field').val();
            if(query)
                queryAndPopulateCourses(query);
        }, 250);
    });
});