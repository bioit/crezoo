<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>         
        <title>Edit Project</title>
    </head>
    <body>
        <h1>Create Project</h1>
        <form action="Controller" method="post">
            <p>
                <input type="hidden" name="pid"/>
                <table>
                    <tr>                    
                        <td>Name</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="name" size="35"/></td>
                    </tr> 
                    <tr>                    
                        <td>Status</td>
                    </tr>
                    <tr>
                        <td><m:status value='E'/></td>
                    </tr> 
                    <tr>                    
                        <td>Comment</td>
                    </tr>
                    <tr>
                        <td><textarea cols="35" rows="6" type="text" name="comm"></textarea></td>
                    </tr>                     
                </table>
            </p>
            <p>
                <m:save saveName="create" cancelName="back"/>
            </p>
        </form>   
    </body>
</html>
