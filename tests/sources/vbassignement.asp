<%
'Test for assignement operators
a = a + b
a = a - b
a = a mod b
a = a * b
a = a / b
a = a & b
'Complex 
Session.Timeout = Session.Timeout + 4
Session.Timeout = Session.Timeout - 4
Session.Timeout = Session.Timeout * 4
Session.Timeout = Session.Timeout / 4
Session.Timeout = Session.Timeout mod 4
'Primitives
a = a + 12
a = a - false
a = a * 12.4
a = a / true
a = a mod 45
a = a & "Hello"
%>
