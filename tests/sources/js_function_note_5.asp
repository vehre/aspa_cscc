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
    if (false) {
        func_inner();
    }
}
func();
//b is concidered visible and a String Object but actually will be "undefined"
//the statement bellow would generate an error
z = b.length;

b = null
function func2() {
    var func_inner = function() {
        var func_inner2 = function() {
            var func_inner3 = function() {
                b = "Hello";
            }
            func_inner3();
        }
        func_inner2();
    }
    if (Request.QueryString("some_param").Count != 0) {
        func_inner();
    }
}
func2();
//b is concidered visible and a String Object but actually will be "undefined"
//if the asp page did not have a parameter called "some_param" in it's URL
//obviously the parser could not possibly know if such parameter exists
//the statement bellow would generate an error in some cases
z = b.length;

%>
