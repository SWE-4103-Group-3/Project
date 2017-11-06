$(document).ready(function() {
    $('#reuse-grid-button').on('click', function (e) {
        //Display Course Search Modal
        $('#search-course-grid-modal')
            .css('display', 'block')
            .css('opacity', '1');

        //Cancel, Close Modal, Restore Previous State
        $('#search-course-grid-modal-close-top')
            .add('#search-course-grid-modal-close-bottom')
            .on('click', function (e) {
                $('#search-course-grid-modal')
                    .css('display', 'none')
                    .css('opacity', '0');

                $('#course-grid-id').attr('value', '');
                $('#search-course-field').val('');
                $('.course-list-queried-item').css('background-color', 'initial');
            });

        //Close Modal After Submit
        $('#search-course-grid-modal-submit-bottom').on('click', function(e) {
            $('#search-course-grid-modal')
                .css('display', 'none')
                .css('opacity', '0');

            $('#course-form-rows').val("1").hide();
            $('#course-form-cols').val("1").hide();
        });

        //Event Handler for Course Line
        var courseLineEventHandler = function(e) {
            $('.course-list-queried-item').css('background-color', 'initial');
            $(this).css('background-color', 'lightgray');

            var courseID = "";
            var classes = $(this).attr('class').split(/\s+/g);
            for(var classIndex = 0; classIndex < classes.length; classIndex++)
                if(classes[classIndex].match(/cid-/g))
                    courseID = classes[classIndex].split(/cid-/g)[1];

            $('#course-grid-id').attr('value', courseID);
        };

        //Query for Courses Using Value From Input Field
        $('#search-course-field').on('input', function(e) {
            var query = $('#search-course-field').val();
            if(!query)
                return;

            $.ajax({
                type: "get",
                url: "/courses/query/" + query,
                dataType: "json",
                success: function (data, status) {
                    var courseList = $('#course-list-queried').empty();
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
                error: function (data, status) {
                    toastr.error('Something went wrong :( Try reloading the page or retry later.');
                }
            });
        });
    });
});