<%@language="JScript"%>
<%
    /*
    TODO:Make the line below to be interpreted correctly
    new Rectangle().getPosition().setX(10);
    */
    new Rectangle().incPos();
    new Point(2).setY(29);
    var rect = new Rectangle;
    rect.incPos();

    var aa = 20;

    function getWidth() {
        return this.width;
    }


    function setWidth(w) {
        this.width = w;
    }


    function getDimension() {
        return Math.pow(this.width, 2);
    }

    function Rectangle() {
        this.pos = new Point(0, 0);
        this.width = 10;
        this.attr = aa;
        this.getDimension = getDimension;
        this.getWidth = getWidth
        this.setWidth = setWidth;
        this.getPosition = getPosition
        this.setPosition = setPosition;
        this.incPos = function() {
            this.pos.x++;
            this.pos.y++;
        }
        this.toString = function() {
            return "[" + this.pos.x 
            + ", " + this.pos.y
            + ", " + (this.pos.x + this.width)
            + ", " + (this.pos.y + this.width) + "]";
        }
    }


    function getPosition() {
        return this.pos;
    }


    function setPosition(x, y) {
        this.pos.setX(x);
        this.pos.setY(y);
    }


    function Point(x, y) {
        this.x = x;
        this.y = y;
        this.setX = _X;
        this.setY = _Y
    }

    function _X(x) {
        this.x = x;
    }

    function _Y(y) {
        this.y = y;
    }

    rect2 = new Rectangle();
    rect2.incPos();
    rect2.getWidth();
    rect2.getPosition();
    var str = rect2.toString();
%>
