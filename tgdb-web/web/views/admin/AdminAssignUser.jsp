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
        <h1>Assign a user to project <jsp:getProperty name="project" property="name"/></h1>
        <form action="Controller" method="post">
            <p>
                <table>
                    <tr>                    
                        <td>User</td>
                        <td>Role</td>
                    </tr>
                    <tr>
                        <td><m:checkbox collection="users" name="user" idGetter="getId" textGetter="getName"/></td>
                        <td><m:checkbox collection="roles" name="role" idGetter="getRid" textGetter="getName"/></td>
                    </tr> 
                </table>
            </p>
            <p>
                <m:save saveName="create" cancelName="back"/>
            </p>
        </form>   
    </body>
</html>
