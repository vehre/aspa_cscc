<%
    sub sub1
        Response.write "Hello from sub1<br>"
    end sub
    
    
    sub sub2(msg)
        Response.write "sub2 says:" & msg & "<br>" 
    end sub
    
    sub sub3(byval msg, byref nVal)
        if nVal = 100 then
            exit sub
        else nVal = nVal + 2
        end if
        Response.write "sub3 says:" & msg & " and nVal is:" _
        & nVal & "<br>"
    end sub
    
    sub sub4()
        'no args and no action
    end sub
%>
