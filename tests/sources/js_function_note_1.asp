<%@language="JScript"%>
<%
function nonGlobal() {
    var a = "Hello";
}
//this will result in an error
b = a.length;
nonGlobal();
//this will result in an error also since a is a local variable
b = a.length;

function globalVar() {
    a = "Hello";
}
//this will result in an error.a is not instanciated yet
b = a.length;
globalVar();
//b will be 5
b = a.length;
%>
