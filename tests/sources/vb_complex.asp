<%
    a = b
    'Test complex replace patterm
    Response.Redirect("http://" & host & "/index.html")
    'Test some functions
    a = abs(30 * 3 * -1) + round(45.567) + round(45.567, 1) * fix(23.456)
    b = Formatcurrency(2345.56)
    'Test nested translates
    Response.write(Request.Form("a"))
    a = abs(fix(23.456))
    Response.write "Hello There"
    Response.write "Formats:[" &_
    Formatcurrency(2345.56) & ", " & Formatcurrency(2345.56, 1) & "]<br>"
%>
