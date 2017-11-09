function Cell(opt) {
    this.el = $('<td class="course-seat"/>');

    this.percent = opt.percent;
    this.row = opt.row;
    this.col = opt.col;
    this.states = opt.states;
    this.parent = opt.parent;
    this.rows = opt.rows;
    this.state = 0;

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
            this.student = null;
            this.el.html("");
        }
    };

    this.getInfo = function() {
        var seat = {
            "id": this.id,
            "row": this.row,
            "col": this.col,
            "state": this.state,
            "course": {
                "id": this.parent.id
            }
        };
        if(this.student) {
            seat.student = {
                "id": this.student.id
            };
        }
        return seat;
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
    };

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
            if(user.hasExtendedPrivileges) {
                return;
            }

            if(seat.hasStudent() && seat.getStudentID() !== user.id) {
                return;
            }

            if(seat.state !== 0) {
                return;
            }

            if(!confirm("are you sure?")) {
                return;
            }

            var info = seat.getInfo();

            // Is the student trying to remove himself?
            if(seat.hasStudent()) {
                info.student = null;
            } else {
                info.student = {
                    "id": user.id
                };
            }

            $.ajax({
                type: "post",
                url: "/courses/"+seat.parent.id+"/seat",
                data: JSON.stringify(info),
                contentType: "application/json",
                success: function(data) {
                    window.location.reload();
                },
                error: function (data, status) {
                    console.error(data);
                }
            });
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

    this.id = opt.id;
    this.rows = opt.rows;
    this.cols = opt.cols;
    this.states = opt.states;
    this.editable = opt.editable;
    this.selectable = opt.selectable;
    this.cells = new Array(); // init seat 2D array

    this.getSeats = function () {
        var seats = [];
        for (var i = 0; i < this.cells.length; i++) {
            for (var j = 0; j < this.cells[i].length; j++) {
                seats.push(this.cells[i][j].getInfo());
            }
        }
        return seats;
    };

    this.setSeats = function (seats) {
        if(!seats) {
            return;
        }

        for (var i = 0; i < seats.length; i++) {
            var cell = this.cells[seats[i].row][seats[i].col];
            cell.setState(seats[i].state);
            cell.setStudent(seats[i].student);
            cell.setID(seats[i].id);
        }
    };

    this.render = function (opt) {
        var percent = 100 / this.cols;
        for (var i = 0; i < this.rows; i++) {
            var row = $('<tr/>');
            this.cells[i] = new Array();
            for (var j = 0; j < this.cols; j++) {
                var cell = new Cell({
                    row: i,
                    col: j,
                    percent: percent,
                    states: this.states,
                    parent: this,
                    rows: this.rows
                });
                row.append(cell.el);
                this.cells[i][j] = cell;
            }
            this.el.append(row);
        }
    };

    this.render();
    this.setSeats(opt.seats);
}
