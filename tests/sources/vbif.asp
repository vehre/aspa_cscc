<%@Language="VBSCript"%>
<%
    a = 40
    b = 30

    if a > b then
        a = b
    elseif a = b then
        a = a + 1
    elseif a = 2 * b then
        a = 3 * b
    else
        a = -b
    end if
    a = a + -b
%>

