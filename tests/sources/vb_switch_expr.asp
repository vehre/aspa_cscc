<%
select case ""
  case request("var1") Response.write "var1"
  case request("var2") Response.write "var2"
  case else Response.write "all vars are ok"
end select
%>
