<%
'Class test
class test
    private const cc = 45 + 676
    private name
    public glob_name

    some_var = 200

    set name = "HELLO"

    public default sub age(byref put_age)
        put_age = 20
        put_age = put_age + 1
    end sub


    public sub age2(today)
        'do somthing
    end sub


    private sub priv_sub(arg)
        a = 300
        exit sub
    end sub


    private sub priv_sub2
        a = 250
    end sub


    sub noname end sub


    function f(a, b, byval d, byref h)
        f = 30
    end function


    function f2()
        f2 = 300
    end function


    public function f3() end function


    public function f4(ggh) end function


    private function f5()
        f5 = "This is private"
    end function


    'Property test
    property get p_age
        p_age = theage
    end property

    private property get p2
    end property

    public property get p3
    end property

    public property get p4
        a = abs(-30)
        p4 = a
    end property

    property get prop_with_args(h3)
        prop_with_args = abs(h3)
    end property

    property set s1(a)
    end property
    
    private property let let1(a, b, c)
        exit property
        a = b + c
        Randomize 34 * 89
    end property
end class
%>
