<%
set cmd = Server.CreateObject("ADODB.Command")
cmd.CommandText = "Select * from tableA"
set con = Server.CreateObject("ADODB.Connection")
cmd.activeconnection = con
set rs = cmd.execute
set rs2 = con.Execute("Select *")
rs.close
rs2.close
con.close
%>