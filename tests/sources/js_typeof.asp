<%@language="javascript"%>
<%
    a = 12;
    b = true;
    c = "Hello";
    d = new Date;
    e = function() {}

    function func(a, b) {
    }


    at = typeof a;
    bt = typeof b
    ct = typeof c
    dt = typeof d
    et = typeof e;

    res = typeof a == "number"
    res = typeof b == "boolean"
    res = typeof c == "string"
    res = typeof d == "object";
    res = typeof e == "function" || typeof func == "function";
    f = "string"
    res = typeof c == f
    res = typeof a != "number";
    res = typeof b != "boolean"
    res = typeof c != "string"
    res = typeof d != "object";
    res = typeof e != "function" && typeof func == "function";
    res = typeof c != f
%>
