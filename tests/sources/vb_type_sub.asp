<%
a = 30
b = "hello"
c = true

d = a or 2
sub s1
	d = a + 30
	d = a or 2
	d = a or c
	a = "hello" 
end sub

d = a or 2

function f
	l = b & a
	d = a or b
	d = a or c
	d = a or 2
	a = "str"
end function
d = a or 2
%>
