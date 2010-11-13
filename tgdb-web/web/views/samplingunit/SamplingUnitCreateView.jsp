<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Collection" %>

<%
    org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller"); 
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <script language="JavaScript" src="javascripts/validate.js"></script>        
        <script language="JavaScript">
            function init() { 
                define('name', 'string', 'Name', 1, 20);
                define('comm', 'string', 'Comment', null, 256);                
            }        
        </script>          
        <title>Create Sampling unit</title>
    </head>
    <body onLoad="init()">
        <h1>Create Sampling unit</h1>
        <form action="Controller" method="post">
            <input type="hidden" name="pid" value='<%=request.getAttribute("pid")%>'/>
            <p>
                <table>  
                    <tr>
                        <td>Species <br>
                            <m:checkbox collection="speciesInterfaces" name="species" idGetter="getSid" textGetter="getName" selected='<%=caller.getSid()%>'/>
                        </td>
                    </tr>                    
                    <tr>                    
                        <td>Status</td>
                    </tr>
                    <tr>
                        <td>
                            <m:status value="E"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Name</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="name" size="35"/></td>
                    </tr>                    
                    <tr>
                        <td>Comment</td>
                    </tr>
                    <tr>
                        <td><textarea type="text" cols="35" rows="6" name="comm"></textarea></td>
                    </tr>                    
                </table>
            </p>
                <m:save saveName="create" cancelName="back"/>
            </p>
        </form> 
    </body>
</html>
