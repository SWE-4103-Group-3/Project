window.onload = function() {
    document.getElementById('student-login-button').addEventListener('click', function() {
        $('#sign-in-form').slideDown(400, function() {
            $('#sign-in-form').css('display', 'flex');
            $('#sign-up-header-text').text(function(index, text) {
                return "Student Sign Up";
            });

            $('#user-type-selector').attr('value', 'false');
            $('#user-type-selector').prop('checked', false);
        });
    });

    document.getElementById('instructor-login-button').addEventListener('click', function() {
        $('#sign-in-form').slideDown(400, function() {
            $('#sign-in-form').css('display', 'flex');
            $('#sign-up-header-text').text(function(index, text) {
                return "Instructor Sign Up";
            });

            $('#user-type-selector').attr('value', 'true');
            $('#user-type-selector').prop('checked', true);
        });
    });

    document.getElementById('sign-up').addEventListener('click', function() {
        $('#sign-up-card').show('slide', {direction: 'left'}, 400, function() {
            $('#sign-up-card').css('display', 'flex');
        });
    });
};