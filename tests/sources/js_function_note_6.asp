<%@language="JScript"%>
<%
function func() {
    var a = 20;
    function func2() {
        var b = 30;
        alert(b + "--" + a); //will promt:30---20
    }
    func2(); //promts:30---20
}
func();
//this is an error
func2();
%>
