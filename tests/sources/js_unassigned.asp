<%@language="JScript"%>
<%
con = Server.CreateObject("ADODB.Connection")
SQL = "UPDATE table"
con.open("tratata")
con.Execute(SQL)
a = con.Execute(SQL)
a.movenext()
%>
