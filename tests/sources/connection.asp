<%@language="JScript"%>
<%
    objConn = Server.CreateObject("ADODB.Connection")
    objConn.ConnectionString = "This is the connection String"
    //unsupported Properties
    objConn.Attributes = 20
    test = objConn.Attributes
    objConn.CommandTimeout = 30
    test = objConn.CommandTimeout
    objConn.ConnectionTimeout = 40
    test = objConn.ConnectionTimeout
    objConn.CursorLocation = 20
    test = objConn.CursorLocation
    objConn.DefaultDatabase = "TheDB"
    test = objConn.DefaultDatabase
    test2 = test.length
    objConn.IsolationLevel = 2
    test = objConn.IsolationLevel
    objConn.Mode = 1
    test = objConn.Mode
    objConn.Provider = "This is the provider"
    test = objConn.Provider
    //this might be better to return the active flag
    test = objConn.State
    vers = objConn.version
    test = vers.length
    a = vers
    test = a.length
    objConn.open()
    objConn.BeginTrans()
    rs = objConn.Execute("SELECT * FROM TABLE")
    objConn.CommitTrans()
    objConn.RollbackTrans()
    objConn.Cancel()
    objConn.open("ConnStr")
    objConn.open("ConnStr", "user")
    objConn.open("ConnStr", "user", "pswd")
    flag = 0
    objConn.open("ConnStr", "user", "pswd", flag)
    rsSchema = objConn.OpenSchema()
%>
