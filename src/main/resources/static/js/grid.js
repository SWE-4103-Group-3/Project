function Cell(opt) {
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

    this.getSeat = function() {
        return {
            "id": this.id,
            "row": this.row,
            "col": this.col,
            "state": this.state,
            "user": {
                "id": this.getUserID()
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

    this.el.on('click', {cell: this}, function (e) {
        cell = e.data.cell;
        if (cell.parent.editable) {
            cell.el.removeClass(cell.states[cell.state]);
            cell.state++;
            if (!cell.states[cell.state]) {
                cell.state = 0;
            }
            cell.el.addClass(cell.states[cell.state]);
        } else if(cell.parent.selectable) {
            if(cell.state == 0 && !cell.hasUser() && confirm("are you sure?")) {
                var seat = cell.getSeat();
                console.log(seat)
                $.ajax({
                    type: "post",
                    url: "/seats",
                    data: JSON.stringify(seat),
                    contentType: "application/json",
                    success: function(data) {
                        if(data === "saved"){
                            cell.setUser(user);
                        }
                        if(data === "removed"){
                            cell.removeStudent();
                        }

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

    this.cells = new Array(); // init cell 2D array

    this.getSeats = function () {
        var seats = [];
        for (var i = 0; i < this.cells.length; i++) {
            for (var j = 0; j < this.cells[i].length; j++) {
                seats.push(this.cells[i][j].getSeat());
            }
        }
        return seats;
    };

    this.setSeats = function (seats) {
        console.log(seats);
        for (var i = 0; i < seats.length; i++) {
            var cell = this.cells[seats[i].row][seats[i].col];
            cell.setState(seats[i].state);
            cell.setUser(seats[i].student);
            cell.setID(seats[i].id);
        }
    };

    this.render = function () {
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
    if (opt.seats) { // initializing seats
        this.setSeats(opt.seats);
    }
}
