<%@language="JScript"%>
<%
with (Request) {
    a = Cookies;
    b = Cookies("a");
    if (Cookies("a").hasKeys) {
        d = Cookies("a")("b")
    }
    h = Cookies + "Hello" + Cookies("a")
    /*
    h += Cookies + "Hello" + Cookies("a")
    h = h + Cookies + "Hello" + Cookies("a")
    */
}
%>
