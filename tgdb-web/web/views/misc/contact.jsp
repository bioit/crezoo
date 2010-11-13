<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>Contact Info</title>
    </head>
    <body>
    
    <%
       org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller");
    %>

    <h1>Contact Info</h1>
    <p></p>
    
    <table class="block">
        
        <tr>
            <td>- Do you need help using the Database?</td>
        </tr>
        <tr>
            <td>- Have you come across a bug that you would like to report?</td>
        </tr>
        <tr>
            <td>- Do you have comments, suggestions or another reason to contact the MMMDb development team?</td>
        </tr>
        <tr>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>If yes click <a href="#"><b>here</b></a></td>
        </tr>
        
    </table>
    </body>
</html>
