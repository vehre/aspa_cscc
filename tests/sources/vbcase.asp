<%@Language="VBSCript"%>
<%
   Select Case MyVar
      CASe 12 Response.write "is 12"
      Case "red", "rose", "#ffgh"     Response.write "red"
      Case "green"   Response.write  "green"
      Case "blue"    Response.write "blue"
      case 13, 45, 89, 1234 Response.write "Is number biger then 12" 
      Case Else      Response.write "pick another color"
   End Select

%>
