<%@language="javascript"%>
<%
a = "Hello"
b = new String("Hello")
c = new String();
test = c.length
test = (a.length + b.length) * c.length * "one".length
b.length = 4;
a.length = 3
c.length = 2
Response.write(a.anchor("Anchor"));
%>
<%=b.anchor("Anchor 2")%>
<%
test = a.big();
test = "<font>" + b.blink() + " </font>";
//Test if a will still be an String Object
a = b.bold();
a += "Hello" + " there".bold()
test = a.charAt(2);
test = a.charCodeAt(2)
test = a.big().anchor("Visit Me") + "<br>"
test = a.concat(c, "Plus")
test = a.concat(c, "Plus").concat("Hello", a, "There").anchor("Visit Me")
test2 = test.charAt(3);
test = a.fixed()
test2 = test.charCodeAt(2)
test = a.fontcolor("red")
test2 = test.charCodeAt(2)
test = a.fontsize(3)
test2 = test.charCodeAt(2)
test = a.fromCharCode()
test2 = test.charCodeAt(2)
test = a.fromCharCode(72, 101, 108, 108, 111); //Hello
test2 = test.charCodeAt(2)
test = a.indexOf("e")
test = a.indexOf("l", 3)
test = a.italics()
//test unsuported method lastIndexOf
test = test.lastIndexOf("a")
test = a.link("This is a link")
//test unsuported method match, replace, search
test = a.match(/\w+/)
test = a.replace(/\W+/, "123")
test = a.search(/ll/)
test = a.slice(4)
test2 = test.charCodeAt(2)
test = a.slice(4, 7)
test2 = test.charCodeAt(2)
test = a.small()
test2 = test.charCodeAt(2)
test = a.split("l")
//check if test is recognized as an array
test2 = test[0]
test2 = test.length
test = a.split("l", 1)
//check if test is recognized as an array
test2 = test[0]
test2 = test.length

test = a.strike();
test2 = test.charCodeAt(0)

test = a.sub();
test2 = test.charCodeAt(0)

test = a.substr(3)
test2 = test.charCodeAt(0)
test = a.substr(3, 5)
test2 = test.charCodeAt(0)

test = a.substring(3, 5)
test2 = test.charCodeAt(0)

test = a.sup()
test2 = test.charCodeAt(0)

test = a.toLowerCase()
test2 = test.charCodeAt(0)

test = a.toUpperCase()
test2 = test.charCodeAt(0)

test = a.toString()
test2 = test.charCodeAt(0)

test = a.valueOf()
test2 = test.charCodeAt(0)
%>
