<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Create Species</h1>
        <form action="Controller" method="post">
            <table>
                <tr>
                    <td>Name</td>
                </tr>
                <tr>
                    <td><input type="text" name="name"></td>
                </tr>
        
                <tr>
                    <td>Comment</td>
                </tr>
                <tr>
                    <td><input type="text" name="comm"></td>
                </tr>
            </table>
            <p>
                <m:save saveName="save" cancelName="back"/>
            </p>
        </form>
    
    
    </body>
</html>
