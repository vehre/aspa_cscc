<%@language="JScript"%>
<%
a = Request.QueryString("some_param").Count != 0
if (Request.QueryString("some_param").Count != 0) {
    a = 20;
} else {
    a = 30;
}
%>
