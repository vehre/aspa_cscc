<%@language="JScript"%>
<%
    var obj = new Object();
    obj.x = 20;
    obj.y = 30;
    obj.testFunc = function() {
        var a = 20;
        a += 30;
    };

    a = new Array(1, 2, 3, 4);
    a.maxElem = function() {
        result = 0;
        for (i = 0; i < this.length; i++) {
            result = Math.max(result, this[i]);
        }
        return result;
    }


    function max() {
        result = 0;
        for (i = 0; i < this.length; i++) {
            result = Math.max(result, this[i]);
        }
        return result;
    }


    //test prototype
    Array.prototype.max = max;
    nArray = new Array(1, 2, 3, 4, 5, 6, 7, 7);

    //test implicit creation[should behave the same]
    bArray = [1, 2, 3, 4, 5, 6, 7, 7];
    b = nArray.max();
    c = bArray.max();
%>
