<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="arx" %>
        <title>Create Project user</title>
    </head>
    <body>
        <h1>Assign a species to a project</h1>
        <form action="Controller" method="post">
            <input type="hidden" name="pid" value='<%=(String)request.getAttribute("pid")%>'/>
            <p>
                <table>
                    <tr>                    
                        <td>Species</td>
                    </tr>
                    <tr>
                        <td><arx:checkbox collection="species" name="species" idGetter="getSid" textGetter="getName"/></td>
                    </tr> 
                </table>
            </p>
            <p>
                <arx:save saveName="create" cancelName="back"/>
            </p>
        </form>   
    </body>
</html>
