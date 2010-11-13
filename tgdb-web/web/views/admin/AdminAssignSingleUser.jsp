<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="project" scope="request" type="org.tgdb.project.projectmanager.ProjectDTO"/>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>         
        <title>Create Project user</title>
    </head>
    <body>
        <jsp:useBean id="user" scope="request" type="org.tgdb.project.projectmanager.UserDTO"/>
        <h1>Assign a user to project <jsp:getProperty name="project" property="name"/></h1>
        <form action="Controller" method="post">
            <p>
                <table>
                    <tr>                    
                        <td>User</td>
                        <td>Role</td>
                    </tr>
                    <tr>
                        <td><jsp:getProperty name="user" property="name"/></td>
                        <td><m:checkbox collection="roles" name="role" idGetter="getRid" textGetter="getName"/></td>
                    </tr> 
                </table>
            </p>
            <p>
                <input type="hidden" name="users" value="<jsp:getProperty name="user" property="id"/>">            
                <m:save saveName="create" cancelName="back"/>
            </p>
        </form>   
    </body>
</html>
