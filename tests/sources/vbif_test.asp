<%@Language="VBSCript"%>
<%
if a > 0 then a = 20

if a > 0 then
end if

if a > 0 then
    a = 20
end if

if a > 0 then a = 20 else    a = 30

if a > 0 then
    a = 20 
else 
    a = 30
end if

if a > 0 then
    if a = 30 then a = 50 else a = 100
    if b = -1 then
        Response.write "b is -1"
    end if
elseif a = -10 then
    Response.write "a is -10"
elseif a = -20 then
    Response.write "a is -10"

elseif a = -30 then
    Response.write "a is -10"
else
    if a < -1000 then Response.write "a less then -1000" else Response.write "a more then -1000"
end if
%>
