
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="users" scope="request" type="java.util.Collection"/>
<jsp:useBean id="project" scope="request" type="org.tgdb.project.projectmanager.ProjectDTO"/>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>
    
    <h1>Users in project '<jsp:getProperty name="project" property="name"/>'</h1>
    
    <p>
        <a href="Controller?workflow=CreateUser"><m:img name="add_24" title="Create a new user"/></a>
        <a href="Controller?workflow=AssignUser"><m:img name="assign_24" title="Assign a new user to "/></a>
    </p>
    
    <form action="Controller" method="post">
        <table class=data>
            <tr class=data>
                <th class=data>User</th>
                <th class=data>Role</th>
                <th class=data>Details</th>
            </tr>
            <m:iterate-collection collection="users">
                <tr class="data #?alt#">
                    <td>#:getName#</td>
                    <td>#:getRoleName#</td>
                    <td><a href="Controller?workflow=ViewUser&id=#:getId#"><m:img name="view"/></a></td>                                      
                </tr>
            </m:iterate-collection>
        </table>
    </form>
    
    </body>
</html>
