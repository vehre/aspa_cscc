<%
set con = Server.CreateObject("ADODB.Connection")
SQL = "UPDATE table"
call con.open("tratata")
call con.Execute(SQL)
a = con.Execute SQL
a.movenext
%>
