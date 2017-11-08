function Seat(opt) {
    this.el = $('<td class="course-seat"/>');

    this.percent = opt.percent;
    this.row = opt.row;
    this.col = opt.col;
    this.states = opt.states;
    this.parent = opt.parent;
    this.rows = opt.rows;
    this.state = 0;
    this.user;

    this.setState = function (state) {
        this.el.removeClass(this.states[this.state]);
        this.state = state;
        this.el.addClass(this.states[this.state]);
    };

    this.setUser = function(user) {
        if(user) {
            this.user = user;
            this.el.html(user.username);
        }
    };

    this.removeStudent = function() {
        if(this.user){
            this.user = undefined;
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
                "id": this.getUserID()
            },
            "course": {
                "id": this.parent.id
            }
        }
    };

    this.setID = function(id) {
        this.id = id;
    };

    this.getUserID = function() {
        if(this.user) {
            return this.user.id;
        }
    };

    this.hasUser = function () {
        return this.user !== undefined;
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
            if(seat.state == 0 && !seat.hasUser() && confirm("are you sure?")) {
                var info = seat.getInfo();
                info.student.id = user.id;
                console.log(info);
                $.ajax({
                    type: "post",
                    url: "/courses/"+seat.parent.id+"/seat",
                    data: JSON.stringify(info),
                    contentType: "application/json",
                    success: function(data) {
                        seat.setUser(user);
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
        if(this.user) {
            this.el.html(this.user.username);
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
            seat.setUser(seats[i].student);
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
