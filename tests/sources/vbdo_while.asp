<%
    a = 0
    do until a > 30
        a = a + 1
    loop
    
    a = 0
    do while a < 30
        a = a + 1
    loop
    
    a = 0
    do
        a = a + 1
    loop until a = 30
    
    a = 0
    do
        a = a + 1
    loop while a < 30

    a = 0
    do
        do while a < 30
            b = b +1
        loop
        a = a + 1
    loop until a = 30


    do
     b = b + 1
     if b > 100 then 
         exit do 
     end if
    loop

'Example from vb help
    Do Until DefResp = vbNo
   MyNum = Int (6 * Rnd + 1)   ' Generate a random integer between 1 and 6.
   DefResp = MsgBox (MyNum & " Do you want another number?", vbYesNo)
Loop

Dim Check, Counter
Check = True: Counter = 0   ' Initialize variables.
Do                            ' Outer loop.
   Do While Counter < 20      ' Inner loop.
      Counter = Counter + 1   ' Increment Counter.
      If Counter = 10 Then    ' If condition is True...
         Check = False        ' set value of flag to False.
         Exit Do              ' Exit inner loop.
      End If
   Loop
Loop Until Check = False      ' Exit outer loop immediately.

'while-whend
Dim Counter
Counter = 0   ' Initialize variable.
While Counter < 20   ' Test value of Counter.
   Counter = Counter + 1   ' Increment Counter.
   Alert Counter
Wend   ' End While loop when Counter > 19.
%>
