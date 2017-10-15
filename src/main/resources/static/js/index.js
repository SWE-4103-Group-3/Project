window.onload = function() {
    $('#student-login-button').click(function() {
        var $userTypeSelector = $('#user-type-selector');

        //Change Sign Up Card Header
        $('#sign-in-header-text').text(function(index, text) {
            return "Student Sign In";
        });

        $('#sign-up-header-text').text(function(index, text) {
            return "Student Sign Up";
        });

        var prev = $('#sign-in-card').is(':visible') && $userTypeSelector.attr('value') === 'true';
        $userTypeSelector.attr('value', 'false');
        $userTypeSelector.prop('checked', false);

        //Do not slide toggle if switching to different context (instructor to student)
        if(prev)
            return;

        $('#sign-in-form').slideToggle(400, function () {
            if ($(this).is(':visible'))
                $(this).css('display', 'flex');
        });
    });

    $('#instructor-login-button').click(function() {
        var $userTypeSelector = $('#user-type-selector');

        $('#sign-in-header-text').text(function(index, text) {
            return "Instructor Sign In";
        });

        $('#sign-up-header-text').text(function(index, text) {
            return "Instructor Sign Up";
        });

        var prev = $('#sign-in-card').is(':visible') && $userTypeSelector.attr('value') === 'false';
        $userTypeSelector.attr('value', 'true');
        $userTypeSelector.prop('checked', true);

        //Do not slide toggle if switching to different context (student to instructor)
        if(prev)
            return;

        $('#sign-in-form').slideToggle(400, function() {
            if($(this).is(':visible'))
                $(this).css('display', 'flex');
        });
    });

    $('#sign-up').click(function() {
        $('#sign-up-card').show('slide', {direction: 'left'}, 400, function() {
            $(this).css('display', 'flex');
        });
    });
};