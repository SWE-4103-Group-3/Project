function Cell(opt) {
    this.el = $('<td class="course-seat"/>');

    this.percent = opt.percent;
    this.row = opt.row;
    this.col = opt.col;
    this.states = opt.states;
    this.parent = opt.parent;
    this.rows = opt.rows;
    this.state = 0;
    this.absent = opt.absent || false;

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

    this.studentIsAbsent = function() {
        return this.absent;
    };

    this.setAbsent = function (setAbsent) {
        if(!this.hasStudent()) {
            return;
        }

        if(setAbsent) {
            this.el.html(this.student.username +' <i class="fa fa-minus-circle"></i>');
            this.absent = true;
        } else {
            this.el.html(this.student.username+' <i class="fa fa-check-circle"></i>');
            this.absent = false;
        }
    };

    this.clearAttendance = function () {
        if(!this.hasStudent()) {
            return;
        }

        this.el.html(this.student.username);
        this.absent = false;
    };

    this.el.on('click', {cell: this}, function (e) {
        var cell = e.data.cell;
        if (cell.parent.editable) {
            if(cell.hasStudent()) {
                return;
            }

            cell.el.removeClass(cell.states[cell.state]);
            cell.state++;
            if (!cell.states[cell.state]) {
                cell.state = 0;
            }
            cell.el.addClass(cell.states[cell.state]);
        } else if(cell.parent.selectable) {
            if(user.hasExtendedPrivileges) {
                return;
            }

            if(cell.hasStudent() && cell.getStudentID() !== user.id) {
                return;
            }

            if(cell.state !== 0) {
                return;
            }

            var seat = cell.getInfo();

            // Is the student trying to remove himself?
            if(cell.hasStudent()) {
                $('#'+removeModalId).modal();
                $('#'+removeButtonId).on('click', function () {
                    seat.student = null;
                    postSeat(seat);
                });

            } else {
                $('#'+setModalId).modal();
                $('#'+setButtonId).on('click', function () {
                    seat.student = {
                        "id": user.id
                    };
                    postSeat(seat);
                });
            }
        } else if(cell.parent.takeAttendance) {
            if(!cell.hasStudent()) {
                return;
            }

            cell.setAbsent(!cell.absent);
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

    if(!removeButtonId || !removeModalId) {
        throw 'place specify removeButtonId and removeModalId';
    }

    if(!setButtonId || !setModalId) {
        throw 'place specify setButtonId and setModalId';
    }

    this.id = opt.id;
    this.rows = opt.rows;
    this.cols = opt.cols;
    this.states = opt.states;
    this.editable = opt.editable;
    this.selectable = opt.selectable;
    this.seats = opt.seats;
    this.takeAttendance = opt.takeAttendance || false;
    this.cells = []; // init seat 2D array

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

    this.clearSeats = function() {
        for (var i = 0; i < this.cells.length; i++)
            for(var j = 0; j < this.cells[i].length; j++)
                this.cells[i][j].setState(0);
    };

    this.removeStudents = function () {
        for (var i = 0; i < this.cells.length; i++) {
            for(var j = 0; j < this.cells[i].length; j++) {
                this.cells[i][j].removeStudent();
            }
        }
    };

    this.getAbsences = function() {
        var absences = [];
        for (var i = 0; i < this.cells.length; i++) {
            for(var j = 0; j < this.cells[i].length; j++) {
                if (this.cells[i][j].studentIsAbsent()) {
                    absences.push({
                        "id": this.cells[i][j].getStudentID()
                    });
                }
            }
        }
        return absences;
    };

    this.getCourseID = function () {
        return this.id;
    };

    this.render = function () {
        var percent = 100 / this.cols;
        for (var i = 0; i < this.rows; i++) {
            var row = $('<tr/>');
            this.cells[i] = [];
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
        this.setSeats(this.seats);
    };

    this.startAttendance = function() {
        this.takeAttendance = true;
        for (var i = 0; i < this.cells.length; i++) {
            for(var j = 0; j < this.cells[i].length; j++) {
                this.cells[i][j].setAbsent(false);
            }
        }
    };

    this.endAttendance = function() {
        this.takeAttendance = false;
        for (var i = 0; i < this.cells.length; i++) {
            for(var j = 0; j < this.cells[i].length; j++) {
                this.cells[i][j].clearAttendance();
            }
        }
    };

    this.render();
}
