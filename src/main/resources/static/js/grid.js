function Cell(opt) {
    this.el = $('<td/>');

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

    this.el.on('click', {cell: this}, function (e) {
        cell = e.data.cell;
        if (cell.parent.editable) {
            cell.el.removeClass(cell.states[cell.state]);
            cell.state++;
            if (!cell.states[cell.state]) {
                cell.state = 0;
            }
            cell.el.addClass(cell.states[cell.state]);
        }
    });

    this.render = function () {
        this.el.css('width', this.percent + '%');
        this.el.css('height', 'calc((85vh - 200px) / ' + this.rows + ')');
        this.el.addClass(this.states[this.state]);
    };

    this.render();
}

function Grid(opt) {
    this.el = $('<table/>');

    this.rows = opt.rows;
    this.cols = opt.cols;
    this.states = opt.states;
    this.editable = opt.editable;

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
            this.cells[seats[i].row][seats[i].col].setState(seats[i].state);
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
