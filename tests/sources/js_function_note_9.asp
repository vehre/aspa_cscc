<%@language="JScript"%>
<%
    function f(a, b, c) {
        return 1;
    }

    function g() {}
    
    f(12, 34);
    f(12);
    f();
    g()
    g(1, 2, 3, 4);
%>
