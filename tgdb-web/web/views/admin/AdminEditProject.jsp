<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="project" scope="request" type="org.tgdb.project.projectmanager.ProjectDTO" />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>         
        <title>Edit Project</title>
    </head>
    <body>
        <h1>Edit Project</h1>
        <form action="Controller" method="post">
            <p>
                <input type="hidden" name="pid" value='<jsp:getProperty name="project" property="pid"/>'/>
                <table>
                    <tr>                    
                        <td>Name</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="name" size="35" value='<jsp:getProperty name="project" property="name"/>'/></td>
                    </tr> 
                    <tr>                    
                        <td>Status</td>
                    </tr>
                    <tr>
                        <td><m:status value='<%=project.getStatus()%>'/></td>
                    </tr> 
                    <tr>                    
                        <td>Comment</td>
                    </tr>
                    <tr>
                        <td><textarea type="text" name="comm" cols="35" rows="6"><jsp:getProperty name="project" property="comm"/></textarea></td>
                    </tr>                     
                </table>
            </p>
            <p>
                <m:save saveName="save" cancelName="back"/>
            </p>
        </form>   
    </body>
</html>
