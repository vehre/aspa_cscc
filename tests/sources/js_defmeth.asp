<%@language="javascript"%>
<%
var objConn = Server.CreateObject("ADODB.Connection");
objConn.Open(adConnStr);
var rs = objConn.Execute("GET_OLD_SPONSORSHIPS");
Response.write(rs("spID")  + rs("Title") + rs("Title").value)
rs.close();
objConn.close();
%>
