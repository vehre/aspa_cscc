<%@language="jscript"%>
<%
cmd = Server.CreateObject("ADODB.Command");
cmd.CommandText = "Select * from tableA";
con = Server.CreateObject("ADODB.Connection")
cmd.activeconnection = con
rs = cmd.execute
rs2 = con.Execute("Select *")
rs.close
rs2.close
con.close
%>