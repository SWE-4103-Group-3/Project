function Cell(opt) {
  this.el = $('<td/>');

  this.percent = opt.percent;
  this.states = opt.states;
  this.state = 0

  this.el.on('click', {cell: this}, function(e) {
    cell = e.data.cell
    cell.el.removeClass(cell.states[cell.state]);
    cell.state++;
    if(!cell.states[cell.state]) {
      cell.state = 0;
    }
    cell.el.addClass(cell.states[cell.state]);
  });

  this.render = function() {
    this.el.css('width', this.percent+'%')
    this.el.css('padding-bottom', this.percent+'%')
    this.el.addClass(this.states[this.state]);
  };

  this.render();
}

function Grid(opt) {
  this.el = $('<table/>');

  this.rows = opt.rows;
  this.cols = opt.cols;
  this.states = opt.states;

  this.cells = new Array(); // init cell 2D array

  this.render = function() {
    var percent = 100 / this.cols;
    for(var i = 0; i < this.rows; i++) {
      var row = $('<tr/>');
      this.cells[i] = new Array();
      for(var j = 0; j < this.cols; j++) {
        var cell = new Cell({
          row: i,
          col: j,
          percent: percent,
          states: this.states
        });
        row.append(cell.el);
        this.cells[i][j] = cell;
      }
      this.el.append(row);
    }
  };

  this.render()
}
