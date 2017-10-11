window.onload = function() {
    document.getElementById('student-login-button').addEventListener('click', displayLoginForm);
    document.getElementById('instructor-login-button').addEventListener('click', displayLoginForm);
    document.getElementById('sign-up').addEventListener('click', displaySignUpCard);
}

function displayLoginForm() {
    $('#sign-in-form').slideDown(400, function() {
        $('#sign-in-form').css('display', 'flex');
    });
}

function displaySignUpCard() {
    console.log('hit');
    $('#sign-up-card').show('slide', {direction: 'left'}, 400, function() {
        $('#sign-up-card').css('display', 'flex');
        console.log('hit');
    });
}