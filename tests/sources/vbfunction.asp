<%
function f1
end function


function f2(num) 
    f2 = num + 2
end function

function f3(num) 
    if num > 0 then 
        f3 = num + 2
    end if
end function


function f4(num) 
    if num > 0 then 
        f4 = num + 2
        exit function
    end if
end function
%>
