<%
a = 30
select case a
  case 2: Response.write "2"
  case 3 Response.write "3"
  case 30: Response.write "30"
  case else: Response.write "IS " & a
end select
%>  