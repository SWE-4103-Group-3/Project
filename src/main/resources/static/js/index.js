window.onload = function() {
    document.getElementById('student-login-button').addEventListener('click', function() {
        $('#sign-in-form').slideDown(400, function() {
            $('#sign-in-form').css('display', 'flex');
            $('#sign-up-header-text').text(function(index, text) {
                return "Student Sign Up";
            });

            if($('#user-type-selector').length)
                $('#user-type-selector').remove();

            $('#sign-up-form').append($('<input id="user-type-selector" type="hidden" name="hasExtendedPrivileges" value="false"/>'));
        });
    });

    document.getElementById('instructor-login-button').addEventListener('click', function() {
        $('#sign-in-form').slideDown(400, function() {
                $('#sign-in-form').css('display', 'flex');
                $('#sign-up-header-text').text(function(index, text) {
                    return "Instructor Sign Up";
                });

                if($('#user-type-selector').length)
                    $('#user-type-selector').remove();

                $('#sign-up-form').append($('<input id="user-type-selector" type="hidden" name="hasExtendedPrivileges" value="true"/>'));
            });
    });

    document.getElementById('sign-up').addEventListener('click', function() {
        $('#sign-up-card').show('slide', {direction: 'left'}, 400, function() {
            $('#sign-up-card').css('display', 'flex');
        });
    });
};