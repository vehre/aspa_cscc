<%@language="javascript"%>
<%
/*
    function aClass() {
        this.a = 20;
    }

    new aClass instanceof aClass
    && new Object instanceof Object
*/
    result = "Hello" instanceof String
    && new Date instanceof Date
    && new Array instanceof Array
    && new Number(23) instanceof Number
    && new Boolean instanceof Boolean;
%>
