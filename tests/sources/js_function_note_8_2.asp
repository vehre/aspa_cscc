<%@language="JScript"%>
<%
var a = 20;
var b = 30;
function func() {
    c = 20;
    var e = -1,h;
    h = 1;
    inFunc = function(l, m) {
        return l + m + postDecl;
    }
    var postDecl = 30;
    inFunc(2, 3);
    return a * b + c / (e + h);
}
%>
