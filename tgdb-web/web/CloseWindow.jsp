<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
   
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>Control Close Window Frame</title>
    </head>
   
    <body>
    
    <%--session is already invalidated by LoginAction servlet but it doesn't hurt to make sure the session is dead, does it?--%>
    <%--plus this jsp page is never generated ;o--%>
    session.invalidate();
    <%--
    <script language="JavaScript">
    window.close();
    </script>--%>
    <tr><td>hey man</td></tr>
    </body>
</html>
