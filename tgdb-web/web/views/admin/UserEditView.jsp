<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="user" scope="request" type="org.tgdb.project.projectmanager.ProjectUserDTO" />
<%
    org.tgdb.project.projectmanager.ProjectUserDTO userDto = (org.tgdb.project.projectmanager.ProjectUserDTO)request.getAttribute("user");
    //String rid = ""+userDto.getRid();
    String status = ""+userDto.getStatus();
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>         
        <title>Edit Project user</title>
    </head>
    <body>
        <h1>Edit Project user</h1>
        <form action="Controller" method="post">
            <p>
                <input type="hidden" name="id" value='<jsp:getProperty name="user" property="id"/>'/>
                <table>
                    <tr>                    
                        <td>Name</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="name" size="35" value='<jsp:getProperty name="user" property="name"/>'/></td>
                    </tr> 
                     <tr>                    
                        <td>Username</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="usr" size="35" value='<jsp:getProperty name="user" property="usr"/>'/></td>
                    </tr> 
                     <tr>                    
                        <td>Password</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="pwd" size="35" value='<jsp:getProperty name="user" property="pwd"/>'/></td>
                    </tr>                     
                    <tr>                    
                        <td>Email</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="email" size="35" value='<jsp:getProperty name="user" property="email"/>'/></td>
                    </tr> 
                    <tr>                    
                        <td>User website</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="userLink" size="35" value='<jsp:getProperty name="user" property="userLinkName"/>'/></td>
                    </tr>    
                    <tr>                    
                        <td>Status</td>
                    </tr>
                    <tr>
                        <td>
                            <m:status value='<%=status%>'/>
                        </td>
                    </tr>     
                    <tr>                    
                        <td>Research group</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="groupName" size="35" value='<jsp:getProperty name="user" property="groupName"/>'/></td>
                    </tr>                 
                    <tr>
                        <td>Address</td>
                    </tr>
                    <tr>
                        <td><textarea type="text" cols="35" rows="6" name="groupAddress"><jsp:getProperty name="user" property="groupAddress"/></textarea></td>
                    </tr>  
                    <tr>                    
                        <td>Phone</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="groupPhone" size="35" value='<jsp:getProperty name="user" property="groupPhone"/>'/></td>
                    </tr>    
                    <tr>                    
                        <td>Group website</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="groupLink" size="35" value='<jsp:getProperty name="user" property="groupLinkName"/>'/></td>
                    </tr>                  
                </table>
            </p>
            <p>
                <m:save saveName="save" cancelName="back"/>
            </p>
        </form>   
    </body>
</html>
