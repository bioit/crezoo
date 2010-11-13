<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="user" scope="request" type="org.tgdb.project.projectmanager.UserDTO" />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>View user</title>
        <script language="JavaScript" src="javascripts/layers.js"></script>
    </head>
    <body>
        <h1>Project user</h1>
    <table class="block">
        <th class="block" colspan="3">
            <m:hide privilege=""> <!-- Endast admin -->
                <a href="Controller?workflow=AdminEditUser&id=<jsp:getProperty name="user" property="id"/>">
                <m:img name="edit"/></a>
            </m:hide>
            &nbsp;
        </th>
        <tr>
            <td class="block">
                <b>Name</b><br> <jsp:getProperty name="user" property="name"/>
            </td>
            <td class="block">
                <b>Email</b><br> <a href="mailto:<jsp:getProperty name='user' property='email'/>"><jsp:getProperty name='user' property='email'/></a>
            </td> 
            <td class="block">
                <b>Personal website</b><br><a href="<jsp:getProperty name='user' property='userLinkUrl'/>" target="_blank"><jsp:getProperty name='user' property='userLinkName'/></a>
            </td>           
        </tr>
        <tr>
            <td colspan="3">
                <br><hr><br>
            </td>
        </tr>
        <tr>
            <td class="block" colspan="2">
                <b>Research group</b><br><jsp:getProperty name="user" property="groupName"/>
            </td>            
            <td class="block">
                <b>Group website</b><br><a href="<jsp:getProperty name='user' property='groupLinkUrl'/>" target="_blank"><jsp:getProperty name='user' property='groupLinkName'/></a>
            </td>                  
        </tr>  
        <tr>
            <td class="block" colspan="3">
                <b>Group address</b><br><jsp:getProperty name="user" property="groupAddress"/>
            </td>        
        </tr>   
        <tr>
            <td class="block" colspan="3">
                <b>Group phone</b><br><jsp:getProperty name="user" property="groupPhone"/>
            </td>            
        </tr>         
    </table>   
    <form action="Controller" method="post">
        <br>
        <m:back/>
    </form>
    </body>
</html>
