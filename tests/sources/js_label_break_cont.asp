<%@language="JScript"%>
<%
var a = new Array()
var i, j, s = "", s1 = ""
Outer:
    for (i = 0; i < 5; i++) {
        Inner:
            for (j = 0; j < 5; j++) {
                if (j == 2) continue Inner
                else {
                    a[i, j] = j + 1;;
                    break Outer;
                }
            }
    }
%>
