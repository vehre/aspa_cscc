<%@language="JScript"%>
<%
    function nestedFunction4() {
        inFucn4();
        inFucn4 = function() {
            alert("inFucn4");
        }
    }

    nestedFunction4();
    nestedFunction5();
    function nestedFunction5() {
        inFucn5();
        inFucn5 = function() {
            alert("inFucn5");
        }
    }
%>
