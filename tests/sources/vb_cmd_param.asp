<%
set conn = Server.CreateObject("ADODB.Connection")
set oCmd = Server.CreateObject("ADODB.Command")
set oCmd.ActiveConnection = conn
oCmd.CommandText = "FORUM_MESSAGE"
oCmd.CommandType = 4
set oParam = oCmd.CreateParameter("MESSAGEID", 3, 1)
oCmd.parameters.append oParam
%>