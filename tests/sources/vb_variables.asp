<%
    a = 0

    sub test1
        a = a + 1
        b = b + 1
    end sub


    function test2
        test2 = a + 1
        a = test2
        b = b + 1
    end function

    b = 0

    Response.write "a=" & a & " b=" & b & "<br>"
    test1
    Response.write "a=" & a & " b=" & b & "<br>"
    c = test2()
    Response.write "a=" & a & " b=" & b  & " c = " & c & "<br>"
%>
