<%
class TestClass
	property let a(val)
		aVar = val
	end property

	property get a
		a = aVar
	end property

	sub setA(val)
		aVar = val
	end sub

	private aVar
	dim bVar
    cVar
end class

set test = new TestClass
test.a = 23
Response.write test.a & "<br>"
test.setA(43)
Response.write test.a & "<br>"
test.a = "Hello"
Response.write test.a & "<br>"
%>
