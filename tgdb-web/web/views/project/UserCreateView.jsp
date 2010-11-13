<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>         
        <title>Create Project user</title>
    </head>
    <body>
        <h1>Create Project user</h1>
        <form action="Controller" method="post">
            <p>
                <table>
                    <tr>                    
                        <td>Name</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="name" size="35" value=''/></td>
                    </tr> 
                     <tr>                    
                        <td>Username</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="usr" size="35" value=''/></td>
                    </tr> 
                     <tr>                    
                        <td>Password</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="pwd" size="35" value=''/></td>
                    </tr>                     
                    <tr>                    
                        <td>Email</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="name" size="35" value=''/></td>
                    </tr> 
                    <tr>                    
                        <td>Website</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="userLink" size="35" value='http://'/></td>
                    </tr>                  
                    <tr>                    
                        <td>Status</td>
                    </tr>
                    <tr>
                        <td>
                            <m:status value='E'/>
                        </td>
                    </tr>     
                    <tr>                    
                        <td>Research group</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="groupName" size="35" value=''/></td>
                    </tr>                 
                    <tr>
                        <td>Address</td>
                    </tr>
                    <tr>
                        <td><textarea type="text" cols="35" rows="6" name="groupAddress"></textarea></td>
                    </tr>  
                    <tr>                    
                        <td>Phone</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="groupPhone" size="35" value=''/></td>
                    </tr>    
                    <tr>                    
                        <td>Website</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="groupLink" size="35" value='http://'/></td>
                    </tr>                  
                </table>
            </p>
            <p>
                <m:save saveName="create" cancelName="back"/>
            </p>
        </form>   
    </body>
</html>
