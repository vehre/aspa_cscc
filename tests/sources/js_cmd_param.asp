<%@language="JScript"%>
<%
conn = Server.CreateObject("ADODB.Connection")
oCmd = Server.CreateObject("ADODB.Command")
oCmd.ActiveConnection = conn
oCmd.CommandText = "FORUM_MESSAGE"
oCmd.CommandType = 4
oParam = oCmd.CreateParameter("MESSAGEID", 3, 1)
//oCmd.parameters.append(oParam)
%>