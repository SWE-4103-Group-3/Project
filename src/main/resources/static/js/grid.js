function Seat(opt) {
    this.el = $('<td class="course-seat"/>');

    this.percent = opt.percent;
    this.row = opt.row;
    this.col = opt.col;
    this.states = opt.states;
    this.parent = opt.parent;
    this.rows = opt.rows;
    this.state = 0;
    this.student;

    this.setState = function (state) {
        this.el.removeClass(this.states[this.state]);
        this.state = state;
        this.el.addClass(this.states[this.state]);
    };

    this.setStudent = function(student) {
        if(student) {
            this.student = student;
            this.el.html(student.username);
        }
    };

    this.removeStudent = function() {
        if(this.student){
            this.student = undefined;
            this.el.html("");
        }
    };

    this.getInfo = function() {
        console.log(this.parent);
        return {
            "id": this.id,
            "row": this.row,
            "col": this.col,
            "state": this.state,
            "student": {
                "id": this.getStudentID()
            },
            "course": {
                "id": this.parent.id
            }
        }
    };

    this.setID = function(id) {
        this.id = id;
    };

    this.getStudentID = function() {
        if(this.student) {
            return this.student.id;
        }
    };

    this.hasStudent = function () {
        return this.student;
    }

    this.el.on('click', {seat: this}, function (e) {
        var seat = e.data.seat;
        if (seat.parent.editable) {
            seat.el.removeClass(seat.states[seat.state]);
            seat.state++;
            if (!seat.states[seat.state]) {
                seat.state = 0;
            }
            seat.el.addClass(seat.states[seat.state]);
        } else if(seat.parent.selectable) {
            if(seat.state == 0 && confirm("are you sure?")) {

                var info = seat.getInfo();

                // Does the seat already have a student?
                if(seat.hasStudent()) {
                    // Is the user clicking on their own seat?
                    if(info.student.id === user.id) {
                        info.student = null;
                    } else {
                        return;
                    }
                } else {
                    info.student.id = user.id;
                }

                console.debug("seat info: " + info);

                $.ajax({
                    type: "post",
                    url: "/courses/"+seat.parent.id+"/seat",
                    data: JSON.stringify(info),
                    contentType: "application/json",
                    success: function(data) {
                        window.location.reload();
                    },
                    error: function (data, status) {
                        console.log(data);
                    }
                });
            }
        }
    });

    this.render = function () {
        //Compute largest of ((85vh - 200) / rows) and (30px)
        var minHeight = ($(window).height() * 0.85) - 200;
        minHeight = minHeight / this.rows;
        minHeight = (minHeight < 30) ? 30 : minHeight;

        this.el.css('width', this.percent + '%');
        this.el.css('height', minHeight + 'px');
        this.el.css('min-width', '80px');
        this.el.addClass(this.states[this.state]);
        if(this.student) {
            this.el.html(this.student.username);
        }
    };

    this.render();
}

function Grid(opt) {
    this.el = $('<table id="course-seating-table"/>');

    this.rows = opt.rows;
    this.cols = opt.cols;
    this.states = opt.states;
    this.editable = opt.editable;
    this.selectable = opt.selectable;
    this.id = opt.id;

    this.seats = new Array(); // init seat 2D array

    this.getSeats = function () {
        var seats = [];
        for (var i = 0; i < this.seats.length; i++) {
            for (var j = 0; j < this.seats[i].length; j++) {
                seats.push(this.seats[i][j].getInfo());
            }
        }
        return seats;
    };

    this.setSeats = function (seats) {
        console.log(seats);
        for (var i = 0; i < seats.length; i++) {
            var seat = this.seats[seats[i].row][seats[i].col];
            seat.setState(seats[i].state);
            seat.setStudent(seats[i].student);
            seat.setID(seats[i].id);
        }
    };

    this.render = function () {
        var percent = 100 / this.cols;
        for (var i = 0; i < this.rows; i++) {
            var row = $('<tr/>');
            this.seats[i] = new Array();
            for (var j = 0; j < this.cols; j++) {
                var seat = new Seat({
                    row: i,
                    col: j,
                    percent: percent,
                    states: this.states,
                    parent: this,
                    rows: this.rows
                });
                row.append(seat.el);
                this.seats[i][j] = seat;
            }
            this.el.append(row);
        }
    };

    this.render();
    if (opt.seats) { // initializing seats
        this.setSeats(opt.seats);
    }
}
