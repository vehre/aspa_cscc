<%
    inc_str = "Hello";
    inc_num = 30;
    inc_date = new Date();

    function incFunc(a, b) {
        return a + b;
    }

    function incClass() {
        this.mem = 30;
        this.method = function() {
            Response.write("method");
        }
        this.ext_method = external_method;
        //defined in the file which include this
        //example of circular dependency
        this.predef = predefFunc;
    }


    function external_method() {
        Response.write("extrenal method");
    }
%>
