<%@language="JScript"%>
<%
function nonGlobalInner() {
    var a = function() {
        var b = 30;
    }
    //is ok to call from here
    a();
}
//it is an error.Method a is uknown
a();
nonGlobalInner();
//it is an error.Method a is local to nonGlobalInner
a();

function globalInner() {
    zz = function() {
        var b = 30;
    }
    //is ok to call from here
    zz();
}
//it is an error.Function a is uknown yet
zz();

globalInner();
//now is ok to call function a.
zz();
%>
