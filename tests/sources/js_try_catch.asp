<%@language="JScript"%>
<%
try {
    a = a + 1;
    try {
        a = a + 2;
        throw "Error";
    } catch (e) {
        throw e + " rethrown";
    } finally {
        b = 0;
    }
} catch (e) {
    a = 20;
} finally {
    b = 1;
}
%>
