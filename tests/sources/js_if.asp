<%@language="JScript"%>
<%
if (a > 0) {
    a = 30;
}

if (a < 40) {
    b = 20;
} else {
    b = 0;
}

if (a == 1) {
    b = 2;
} else if (a == 2) {
    b = 3;
} else if (a == 3) {
    b = 4;
} else if (a == 4) {
    b = 5;
} else {
    b = a + 1;
}
%>
