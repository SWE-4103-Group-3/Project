
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
                $('#courseCols').val('1');
                $('#courseRows').val('1');
                $('#course-form-rows').hide();
                $('#course-form-cols').hide();
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
});

