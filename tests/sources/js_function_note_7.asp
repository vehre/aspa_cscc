<%@language="JScript"%>
<%
var a = getMax(12, 34);
varOuter();
var b = varOuterFounction(12.43);
function getMax(a, b) {
    return a > b ? a : b;
}

function varOuter() {
    varOuterFounction = function (a) {
        return a;
    }
}
//test that if an anonymous function is called prior to it's definition
//the references are fixed only if the containing function was also called
var c = varOuterFounction2();
function varOuter2() {
    varOuterFounction2 = function (a) {
        return a;
    }
}
%>
