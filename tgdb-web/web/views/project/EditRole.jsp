<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="role" scope="request" type="org.tgdb.project.projectmanager.RoleDTO"/>
<jsp:useBean id="privs" scope="request" type="java.util.Collection"/>
<jsp:useBean id="restPrivs" scope="request" type="java.util.Collection"/>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <script language="JavaScript" src="validate.js"></script>
        <script language="JavaScript">
            function init() { 
                define('name', 'string', 'Name', 1, 20);
                define('comm', 'string', 'Comment', null, 255);                
            }        
        </script>          
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body onLoad="init()">

    <h1>Edit Role</h1>
    
    <form action="Controller" method=post>
        <input type="hidden" name="rid" value="<%=role.getRid()%>">
        <p>
            Name<br>
            <input type=text name="name" value="<%=role.getName()%>"><br>
            Comment<br>
            <input type=text name="comm" value="<%=role.getComm()%>"><br>
            
            <p>
            <m:save saveName="submit" onClick="validate();return returnVal;"/>
            &nbsp;
            <a href="Controller?workflow=RemoveRole&rid=<%=role.getRid()%>"><m:img name="delete_24" title="Remove this role"/></a>
            </p>
        </p> 
        <%--<m:select-view name="123" list1="" list2=""/>--%>
        
        <table>
            <tr>
                <td>
                    Privileges<br>
                    <select name="other" multiple size="20">
                        <m:iterate-collection collection="<%=restPrivs%>">
                            <option value="#:getPrid#" title="#:getComm#">#:getName#</option>
                        </m:iterate-collection>
                    </select>
                </td>
                <td>
                    <input id="button" type=submit name="add" value="->"><br>
                    <input id="button" type=submit name="remove" value="<-"><br>
                </td>
                <td>
                    Assigned Privileges<br>
                    <select name="ass" multiple size="20">
                        <m:iterate-collection collection="<%=privs%>">
                            <option value="#:getPrid#" title="#:getComm#">#:getName#</option>
                        </m:iterate-collection>
                    </select>
                </td>
            </tr>
        </table>        
    </form>

    </body>
</html>
