<%@Language="VBSCript"%>


<%

Dim objConn

'COMMENT
REM This is a comment
Response.Expires = 0

teamID = Request.QueryString("ID")

Set objConn = Server.CreateObject("ADODB.Connection")
objConn.Open adConnStr

Set cm = Server.CreateObject("ADODB.Command")
Set rs = Server.CreateObject("ADODB.Recordset")

    set rs = Server.CreateObject("ADODB.Recordset")
    cm.CommandText = "MUNDIAL_GET_GOALS_BY_TEAM"
    cm.CommandType = adCmdStoredProc
    cm.ActiveConnection = objConn
    cm.Parameters.Append(cm.CreateParameter("teamID",4,1,4,teamId))
    set rs = cm.Execute()


%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1253">
  <link REL="stylesheet" HREF="main.css" TYPE="text/css">
<title>WORLD CUP 2002 - ΕΡΤ online</title>
</head>

<body bgcolor="#F7F7F7" leftmargin=0 topmargin=0>
<table width="100%" border="0" cellspacing="0" cellpadding="0">

  <tr>
    <td width="5%" height="*"></td>
    <td width="*" height="*" valign="top" align="center">


  <% IF rs.EOF = TRUE THEN %>
    
    <table border="0" width="100%" cellpadding="0" cellspacing="0" class="text">
    <tr><td colspan=2>&nbsp;</td></tr>
    <tr>      
      <td width="50%" height="20"><p align="left"><br><b>ΛΙΣΤΑ ΓΚΟΛ: </b></td>
      <td width="20%" height="20">&nbsp;</td>
    </tr>
    <tr><td colspan=2>&nbsp;</td></tr>
  </table>

  <table border="0" width="100%" cellpadding="4" cellspacing="1">
    <tr>
      <td colspan=8 align=right><a class="list" href=teamsgoal_add.asp?action=ADD&team_id=<% = teamID %>>
        ΝΕΑ ΕΓΓΡΑΦΗ <img src='images/icon_addnew.gif' border=0 alt='προσθεση'></a></td>
    </tr>
    <tr class="textHD">
      <td width=150 align=left>Ημερομηνία - Ώρα</td>
      <td width=20 align=left>No</td>
      <td width=200 align=left>Παίκτης</td>
      <td width=300 align=left>Αγώνας</td>
      <td width=150 align=left>Τύπος γκολ</td>
      <td width=150 align=left>Φάση αγώνα</td>
      <td width=20  align=center>&nbsp;</td>
      <td width=20  align=center>&nbsp;</td>
    </tr>
  </table>
  <% ELSE%>
  <table border="0" width="100%" cellpadding="0" cellspacing="0" class="text">
    <tr><td colspan=2>&nbsp;</td></tr>
    <tr>      
      <td width="50%" height="20"><p align="left"><br><b>ΛΙΣΤΑ ΓΚΟΛ: </b></td>
      <td width="20%" height="20">&nbsp;</td>
    </tr>
    <tr><td colspan=2>&nbsp;</td></tr>
  </table>

  <table border="0" width="*" cellpadding="4" cellspacing="1">
    <tr>
      <td colspan=8 align=right><a class="list" href='teamsgoal_add.asp?action=ADD&team_id=<% = teamID %>'>
        ΝΕΑ ΕΓΓΡΑΦΗ <img src='images/icon_addnew.gif' border=0 alt='προσθεση'></a></td>
    </tr>
    <tr class="textHD">
      <td width=150 align=left>Ημερομηνία - Ώρα</td>
      <td width=20 align=left>No</td>
      <td width=200 align=left>Παίκτης</td>
      <td width=300 align=left>Αγώνας</td>
      <td width=150 align=left>Τύπος γκολ</td>
      <td width=150 align=left>Φάση αγώνα</td>
      <td width=20  align=center>&nbsp;</td>
      <td width=20  align=center>&nbsp;</td>
    </tr>
    <%i=0%>
    <%Do While Not rs.EOF%>
    <%i=i+1%>
    <tr class="textTR<%if i mod 2 = 0 then%>Odd<%else%>Even<%end if%>">
      <td align=left><%=rs("GOAL_TIME")%></td>
      <td align=left><%=rs("PLAYER_NUMBER")%></td>
      <td align=left><%=rs("NAME")%></td>
      <td align=left><%=rs("GAME")%></td>
      <td align=left><%=rs("GOAL_TYPE")%></td>
      <td align=left><%=rs("RESULT_TYPE")%></td>
      <td align=center><a class="list" href="teamsgoal_add.asp?id=<%=rs("ID")%>&action=UPDATE&team_id=<% =teamID%> ">
      <img src='images/icon_edit.gif' border=0 alt='ενημερωση'></a></td>
      <td align=center><a class="list" href="teamsgoal_add.asp?id=<%=rs("ID")%>&action=DELETE&team_id=<% =teamID%>">
      <img src='images/icon_delete.gif' border=0 alt='διαγραφη'></a></td>
    </tr>
      <%rs.MoveNext()%>
    <%Loop%>
    <% END IF %>
  </table>

  
  
    </td>
    <td width="5%" height="*"></td>
  </tr>
  <tr>
    <td height="5%"></td>
    <td height="5%">&nbsp;</td>
    <td height="5%"></td>
  </tr>
</table>
</body>

</html>

<%
Set rs = Nothing
Set cm = Nothing
Set objConn = Nothing
%>
