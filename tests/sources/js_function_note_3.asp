<%@language="JScript"%>
<%
function nested() {
    var a = 20;
    var b = "Hello";
    var innerFunc = function() {
        var z = a + b.length; //bouth a and b are visible
    }
    //the oposite is not true.z is not visible to nested
    innerFunc();
    var h = z; //h will be "undefined"
}
%>
