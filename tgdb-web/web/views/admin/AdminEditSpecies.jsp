<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>
    <jsp:useBean id="species" scope="request" type="org.tgdb.adminmanager.SpeciesDTO"/>
        <h1>Edit Species</h1>
        <form action="Controller" method="post">
            <table>
                <tr>
                    <td>Name</td>
                </tr>
                <tr>
                    <td><input type="text" name="name" value="<jsp:getProperty name="species" property="name"/>"></td>
                </tr>
        
                <tr>
                    <td>Comment</td>
                </tr>
                <tr>
                    <td><textarea type="text" cols="35" rows="6" name="comm"><jsp:getProperty name="species" property="comm"/></textarea></td>
                </tr>
            </table>
            <input type="hidden" name="sid" value="<jsp:getProperty name="species" property="sid"/>"
            <p>
                <m:save saveName="save" cancelName="back"/>
            </p>
        </form>
    
    
    </body>
</html>
