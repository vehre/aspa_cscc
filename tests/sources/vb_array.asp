<%
dim a(12)
dim b
a(0) = 20
a(1) = 30
b = a(0) + a(1)

function f
    dim z(2)
    z(0) = 1
    z(1) = 2
    f = z(0) + z(1)
end function
'The parser should think this is a method call
z(1) = 12
sub s
    dim z(2)
    z(0) = 1
    z(1) = 2
end sub
'The parser should think this is a method call
z(1) = 12
%>
