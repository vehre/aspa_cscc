<%
class TestClass
    dim arr(23)
end class

test = new TestClass
test.arr(2) = 20
a = test.arr
b = test.arr(1)
%>
