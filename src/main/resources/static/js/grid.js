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
            cell.setUser(user);
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
                seats.push({
                    "row": this.cells[i][j].row,
                    "col": this.cells[i][j].col,
                    "state": this.cells[i][j].state
                });
            }
        }
        return seats;
    };

    this.setSeats = function (seats) {
        for (var i = 0; i < seats.length; i++) {
            var cell = this.cells[seats[i].row][seats[i].col];
            cell.setState(seats[i].state);
            cell.setUser(seats[i].student);
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
