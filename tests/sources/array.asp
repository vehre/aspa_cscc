<%@language="javascript"%>
<%
//empty array
a = new Array()
//array with initial length 4
b = new Array(4)
//array populated with values provided
c = new Array(1, 2, 4, 8, "Hello", "there", true, false)
//implicit array creation
d = [];
//implicit array creation with one DINT
e = [2];
//implicit array creation populated with values provided
f = [1, 2, 4, 8, "Hello", "there", true, false]

test = a.concat();
test2 = test.length

test = d.concat();
test2 = test.length

test = a.concat(1, 3, true, "Hello", new Date());
test2 = test.length

test = d.concat(true, false)
test2 = test.length

test = d.concat("one", [1, 2, 4], true)
test = test.join(", ");
test2 = test.substring(1, 4)

test = a.pop();
//test later if test is treated as a date
test = f.push(1, 3, "dd");
test = f.reverse();
test2 = test.length

test = a.shift()

test = a.slice(3)
test2 = test.length

test = a.slice(3, 5)
test2 = test.length

test = a.sort();
test2 = test.length
//sort with user defined function can not be tested yet
//because functions are not supported

test = a.splice(0, 3)
test2 = test.length

test = f.splice(0, 3, "a1", 2, "b3")
test2 = test.length

test = f.unshift(1, 2, 3, 5);
test2 = test.length

test = c.toString()
test2 = test.substring(0, 2)

test = f.valueOf()
test2 = test.substring(0, 2)

test = f.length
f.length = 2
%>
