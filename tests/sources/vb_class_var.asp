<%
class TestClass
    pubVar
    dim dimVar
end class
test = new TestClass
test.pubVar = 30
test.dimVar = 20
a = test.pubVar + test.dimVar
%>
