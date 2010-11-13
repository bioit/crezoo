<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="rapp" scope="request" type="org.tgdb.model.modelmanager.ResearchAppDTO" />   
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>

        <h1>Research Application</h1>
    
        <form action="Controller" method="post">
            <table class="block">
                <th class="block" colspan="2">
                    <m:hide privilege="MODEL_W" pid="<%=rapp.getPid()%>">
                        <a href="Controller?workflow=EditResearchApp&raid=<jsp:getProperty name="rapp" property="raid"/>"><m:img name="edit" title="Edit this research application"/></a>
                        <a href="Controller?workflow=RemoveResearchApp&raid=<jsp:getProperty name="rapp" property="raid"/>" onClick="return confirm('Remove research application?')"><m:img name="delete" title="Remove this research application"/></a>
                    </m:hide>
                    &nbsp;
                </th>
                <tr class="block">
                    <td class="block">
                        <b>Name</b><br> <jsp:getProperty name="rapp" property="name"/>
                    </td>
                </tr>
                <tr class="block">
                    <td class="block">
                        <b>Comment</b><br> <jsp:getProperty name="rapp" property="comm"/>
                    </td>            
                </tr>  
            
                <tr class="block">
                    <td class="block">                
                        <b>User</b><br> <a href="Controller?workflow=ViewUser&id=<jsp:getProperty name="rapp" property="userId"/>" title="View the details for this user"><jsp:getProperty name="rapp" property="userName"/></a> 
                    </td>            
                </tr> 
                <tr class="block">
                    <td class="block">
                        <b>Last updated</b><br> <jsp:getProperty name="rapp" property="ts"/>
                    </td>            
                </tr>               
            </table>
        </form>
    </body>
</html>
