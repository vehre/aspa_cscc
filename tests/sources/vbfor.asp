<%
    for a = abs(50) to 2 ^ 100
        b = b + 1
    next
    
    for a = 50 to 100 * 2 + 67 step 1
        b = b + 1
    next
    
    for a = 50 to 100 step +10
        b = b + 1
    next
    
    for a = 50 to 100 step -1
        b = b + 1
    next

    for a = getInit() to getMax step 2 ^ getStep()
        b = b + 1
    next

    while_step = 2 ^ getStep()
    while_lim = getInit()
    do
        b = b + 1
        a = a + while_step
    loop while a <> while_lim

    for each a in someArray
        b = b + a
    next

    someArray = Request.Cookies
    'TODO this is translated as $_SERVER[HTTP_COOKIE] when it should be $_COOKIE
    for each a in Request.Cookies
        b = b + a
    next a

    for each a in someArray
        b = b + a
    next b

%>
