<%@language="JScript"%>
<%
x = Math.cos(3 * Math.PI) + Math.sin(Math.LN10)
y = Math.tan(14 * Math.E)

with (Math) {
    x = cos(3 * PI) + sin(LN10)
    y = tan(14 * E)
}

with (new Date()) Response.write("Time is:" + getDay())

with (new Date()) with (Response) write("Time is:" + getDay())

var d = new Date();
with (d) with (Response) write("Time is:" + getDay())
%>
