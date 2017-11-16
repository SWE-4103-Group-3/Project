$(document).ready(function () {
    $('#student-login-button').click(function () {
        var $userTypeSelector = $('#user-type-selector');

        $('#sign-up-header-text').text(function (index, text) {
            return "Student Sign Up";
        });

        var prev = $('#sign-in-card').is(':visible') && $userTypeSelector.attr('value') === 'true';
        $userTypeSelector.attr('value', 'false');
        $userTypeSelector.prop('checked', false);

        //Do not slide toggle if switching to different context (instructor to student)
        if (prev)
            return;

        $('#sign-in-form').slideToggle(400, function () {
            if ($(this).is(':visible'))
                $(this).css('display', 'flex');
        });
    });

    $('#instructor-login-button').click(function () {
        var $userTypeSelector = $('#user-type-selector');

        $('#sign-up-header-text').text(function (index, text) {
            return "Instructor Sign Up";
        });

        var prev = $('#sign-in-card').is(':visible') && $userTypeSelector.attr('value') === 'false';
        $userTypeSelector.attr('value', 'true');
        $userTypeSelector.prop('checked', true);

        //Do not slide toggle if switching to different context (student to instructor)
        if (prev)
            return;

        $('#sign-in-form').slideToggle(400, function () {
            if ($(this).is(':visible'))
                $(this).css('display', 'flex');
        });
    });

    $('#sign-up').click(function () {
        //If user had a signin error and wants to sign up, redirect to homepage to select account type
        if ($('#login-error').length === 0) {
            $('#sign-up-card').show('slide', {direction: 'left'}, 400, function () {
                $(this).css('display', 'flex');
            });
        }
        else
            window.location.replace("/");
    });
});