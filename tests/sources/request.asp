<%
    'Test ServerVariables
    a = Request.ServerVariables("ALL_HTTP")
    a = Request.ServerVariables("ALL_RAW")
    a = Request.ServerVariables("APPL_MD_PATH")
    a = Request.ServerVariables("APPL_PHYSICAL_PATH")
    a = Request.ServerVariables("HTTPS")
    a = Request.ServerVariables("INSTANCE_ID")
    a = Request.ServerVariables("INSTANCE_META_PATH")
    a = Request.ServerVariables("SERVER_PORT_SECURE")
    a = Request.ServerVariables("URL")
    a = Request.ServerVariables("CERTHTTP_COOKIE_VARS")
    a = Request.ServerVariables("CERT_FLAGS")
    a = Request.ServerVariables("CERT_ISSUER")
    a = Request.ServerVariables("CERT_KEYSIZE")
    a = Request.ServerVariables("CERT_SECRETKEYSIZE")
    a = Request.ServerVariables("CERT_SERIALNUMBER")
    a = Request.ServerVariables("CERT_SERVER_ISSUER")
    a = Request.ServerVariables("CERT_SERVER_SUBJECT")
    a = Request.ServerVariables("HTTPS_KEYSIZE")
    a = Request.ServerVariables("HTTPS_SECRETKEYSIZE")
    a = Request.ServerVariables("HTTPS_SERVER_ISSUER")
    a = Request.ServerVariables("HTTPS_SERVER_SUBJECT")
    a = Request.ServerVariables("LOGON_USER")
    a = Request.ServerVariables("AUTH_PASSWORD")
    a = Request.ServerVariables("AUTH_TYPE")
    a = Request.ServerVariables("AUTH_USER")
    a = Request.ServerVariables("CONTENT_LENGTH")
    a = Request.ServerVariables("CONTENT_TYPE")
    a = Request.ServerVariables("GATEWAY_INTEFACE")
    a = Request.ServerVariables("HTTP_ACCEPT")
    a = Request.ServerVariables("HTTP_ACCEPT_LANGUAGE")
    a = Request.ServerVariables("HTTP_USER_AGENT")
    a = Request.ServerVariables("HTTP_REFERER")
    a = Request.ServerVariables("LOCAL_ADDR")
    a = Request.ServerVariables("PATH_INFO")
    a = Request.ServerVariables("PATH_TRANSLATED")
    a = Request.ServerVariables("QUERY_STRING")
    a = Request.ServerVariables("REMOTE_ADDR")
    a = Request.ServerVariables("REMOTE_HOST")
    a = Request.ServerVariables("REMOTE_USER")
    a = Request.ServerVariables("REQUEST_METHOD")
    a = Request.ServerVariables("SCRIPT_NAME")
    a = Request.ServerVariables("SERVER_NAME")
    a = Request.ServerVariables("SERVER_PORT")
    a = Request.ServerVariables("SERVER_PROTOCOL")
    a = Request.ServerVariables("SERVER_SOFTWARE")
    a = Request.ServerVariables("SOME_VAR")
    a = Request.ServerVariables("SOME_VAR" & 30)

    'Test Cookies
    a = Request.Cookies
    a = Request.Cookies("a")
    a = Request.Cookies("a").HasKeys
    a = Request.Cookies("a")("b")

    'Test Post params
    a = Request.Form
    a = Request.Form("a")
    a = Request.Form("a").Count
    a = Request.Form("a")(1)

    'Test Put params
    a = Request.QueryString
    a = Request.QueryString("a")
    a = Request.QueryString("a").Count
    a = Request.QueryString("a")(1)

    'Test put or post params
    a = Request("A")
    a = Request("A").Count
    a = Request("A")(1)
%>
