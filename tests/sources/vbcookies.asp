<%
a = Request.Cookies(34)
a = Request.Cookies
a = Request.Cookies("c") & Request.Cookies("d")
a = Request.Cookies(b)(c)
a = Request.Cookies("hello")
a = Request.Cookies("hello")("there")
a = Request.Cookies("b")
a = Request.Cookies(b).hasKeys or Request.Cookies("ccokie").hasKeys
d = "Hello" & Request.Cookies("a") & Request.Cookies("b") & Request.Cookies("a")("c")
tyext = Request.Form & Request.Cookies("some_cookie") & Request.Form("a") & Request.Form("b")(0)
hasParams = Request.Cookies("a").hasKeys or Request.Form("a").Count > 0 or Request.QueryString("a").Count = 1
%>

