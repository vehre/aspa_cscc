<%
class Paralilogram
    private xVar

    property set x(val)
        xVar = val
    end property
    private heightVar
    
    property get x
        x = xVar
    end property


    private yVar
    sub aSub
    end sub
    property get dimension
        dimension = widthVar * heightVar
    end property


    property get width
        width = widthVar
    end property

    property let width(val)
        widthVar = width
    end property
    private widthVar
end class

p = new Paralilogram
p.width = 20
a = p.height
a = p.dimension
%>
