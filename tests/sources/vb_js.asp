<script language="javascript" runat="Server">
var var_arr = [1, 2, 3, 4, 5];
var var_arr2 = new Array(1, 2, 3, 4, 5);
var var_date = new Date();
var var_str = "Hello";
var var_str2 = new String("There");
var var_num = 30;
var var_num2 = 30.4;

function func() {
    return 12;
}
var var_func = function() {
    return 122;
}; //semi required here.Should be fixed

function class_prod() {
  this.x = 0;
  this.toString = function() {
	return "my x=" + this.x;
  }
}
prod = new class_prod()
</script>
<%
    for each el in var_arr
        Response.write el
        Response.write "&nbsp"
    next
    Response.write "<br>"
    'works

    for each el in var_arr2
        Response.write el
        Response.write "&nbsp"
    next
    Response.write "<br>"
    'works

    'date2 = dateAdd("yyyy", 1, var_date)
    'thinks is a string
    Response.write var_str & "&nbsp" & var_str2 & "<br>"

    sum = var_num + var_num2
    Response.write "Sum:" & sum & "<br>"

    Response.write "func:" & func() & " var_func:" & var_func() & "<br>"
    'Test if not parenthesis in function call
    Response.write "func:" & func & " var_func:" & var_func & "<br>"
    'Displays the code of the functions 
    
    'Response.write new class_prod 
    'the class is not visible
    Response.write prod & "<br>"

    sub sub1
        Response.write "sub1<br>"
    end sub

    function vb_func()
        vb_func = 654
    end function

    vb_str = "vb_str"
    vb_num = 34
    vb_date = #12-3-2004#
    dim vb_array(3)
    vb_array(0) = 1
    vb_array(1) = 2
    vb_array(3) = 3
    class test
    end class
%>
<script language="javascript" runat="Server">
Response.write(
    //"sum:" + sum.toString()
    //sum is not visible
    //"<br>vb_str.length:" + vb_str.length
    //vb_string is not visible
    //"<br>vb_num:" + vb_num.toString()
    //vb_num is not visible
    //"<br>vb_date Month:" + vb_date.getMonth()
    //vb_date is not visible
    //"<br>vb_array[0]:" + vb_array[0]
    //vb_array is not visible
    //"<br>vb_func:" + vb_func()
    //vb_func() is not visible
    "<br>");
    //sub1();
    //sub1 is not visible
    //var a = new test()
    //class test is not visible
</script>
