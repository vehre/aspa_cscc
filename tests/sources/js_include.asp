<%@language="JScript"%>
<%
    function predefFunc() {
    }
%>
<!--#include file="def.inc" -->
<%
    a = inc_str.length;
    b = inc_num.toString();
    c = inc_date.getMonth();
    incFunc();
    e = new incClass();
    Response.write("mem:" + e.mem);
    e.method();
    e.ext_method();
    e.predef();
%>
