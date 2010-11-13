<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="role" scope="request" type="org.tgdb.project.projectmanager.RoleDTO"/>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <script language="JavaScript" src="validate.js"></script>
        <script language="JavaScript">
            function init() { 
                define('name', 'string', 'Name', 1, 20);
                define('comm', 'string', 'Comment', null, 255);                
            }        
        </script>          
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body onLoad="init()">

    <h1>Create Role</h1>
    
    <form action="Controller" method=post>
        <input type="hidden" name="rid" value="<%=role.getRid()%>">
        <table>
        <tr>
            <td>
                Name<br>
                <input type=text name="name" value="<%=role.getName()%>">
            </td>
        </tr>
        <tr>
            <td>
                Comment<br>
                <textarea type="text" cols="35" rows="6" name="comm"></textarea>
            </td>
        </tr>
        </table>
            <p>
                <m:save saveName="create" cancelName="back"/>
            </p>
        
    </form>

    </body>
</html>
