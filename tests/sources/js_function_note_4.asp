<%@language="JScript"%>
<%
function func() {
    var func_inner = function() {
        var func_inner2 = function() {
            var func_inner3 = function() {
                b = "Hello";
            }
            func_inner3();
        }
        func_inner2();
    }
    func_inner();
}
func();
//b is visible and is a String Object
z = b.length;
b = null;

function func2() {
    var func_inner = function() {
        var func_inner2 = function() {
            var func_inner3 = function() {
                b = "Hello";
            }
        }
        func_inner2();
    }
    func_inner();
}
func2();
//b is not visible
z = b.length;

function func3() {
    var func_inner = function() {
        var func_inner2 = function() {
            var func_inner3 = function() {
                b = "Hello";
            }
            func_inner3();
        }
    }
    func_inner();
}
func3();
//b is not visible
z = b.length;
b = null

function func4() {
    var func_inner = function() {
        var func_inner2 = function() {
            var func_inner3 = function() {
                b = "Hello";
            }
            func_inner3();
        }
        func_inner2();
    }
}
func4();
//b is not visible
z = b.length;
%>
