<%@language="JScript"%>
<%
    for (a = 1; a < 100; a++) {
    }
    
    for (var a = 20; a != 100; a += 20) {
    }

    b = [1, 2, 3, 4, 5];
    for (a in b);

    for (; a < 100; a++);
    for (;;a++);
    for (;;);
%>
