<%@language="JavaScript"%>
<%
Response.Buffer = true
Response.CacheControl = "Public"
cache = Response.CacheControl
Response.ContentType = "text/plain"
cType = Response.ContentType
a = Response.isClientConnected
Response.status = "Some Status"
Response.pics = "pics"
Response.Expires = "123"
Response.ExpiresAbsolute = "12/2/2004"
Response.addHeader("MyHeader", "HeaderValue")
Response.AppendToLog("This is my message")
Response.Clear()
Response.Clear
Response.end
Response.End()
Response.Flush
Response.Flush()
Response.redirect("Some url")
Response.write(html)
Response.Cookies("A") = "Value"
Response.Cookies("A")("B") = "Other value"
%>
