$(document).ready(function () {
    $('.datepicker').pickadate({
        formatSubmit: 'yyyy-mm-dd',
        hiddenName: true
    });

    $('.timepicker').pickatime({
        twelvehour: true
    });

    $("#createButton").click(function (){
        $("#courseForm").slideToggle();
    });
});