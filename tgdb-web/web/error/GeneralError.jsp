<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd"><jsp:useBean id="exception" scope="request" class="java.lang.Exception"/>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!--link rel="stylesheet" type="text/css" href="test.css" /-->
        <title>CreZOO Error</title>
    </head>
    <body>
    <form action="Controller?workflow=reboot" method="post" target="_top">
        <table>
            <tr valign="bottom">
                <td class="head">CreZOO Error</td>
            </tr>
        </table>
        <hr id="ruler">
        <table>
            <tr>
                <td>&nbsp;Please <input id="buttonclean" type="submit" name="back" value="restart CreZOO" title="Restart CreZOO"> or try again later.</td>
            </tr>
        </table>
    </form>
    </body>
</html>
