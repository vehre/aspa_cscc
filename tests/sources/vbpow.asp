<%
'Test for int_divide and exponential operatores
'Since PHP has no equvalent operatores, those are converted into
'method calls
a = b ^ 23
a = 12 ^ 5
a = a ^ 4
a =a ^ b
a = a ^ a
a = a ^ Session.Timeout
a = a ^ Request.Cookies("count")
'Test int divide
a = b \ 23
a = 12 \ 5
a = a \ 4
a = a \ b
a = a \ a
a = a \ Session.Timeout
a = a \ Request.Cookies("count")
%>

