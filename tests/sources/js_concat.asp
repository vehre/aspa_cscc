<%@language="JScript"%>
<%
a = 20 + 40;
b = 20 + "Hello";
c = 20 + 40 * 4 - 100 + "text" + 30;
d = 20 + 40 * 4 - 100 + "text" + (30 * 2) / 4;
e = a + b + c;
Response.write(a + "<br>" + b + "<br>" + c + "<br>" + d + "<br>" + e);
%>
