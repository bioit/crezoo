<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="roles" scope="request" type="java.util.Collection"/>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>

    <h1>View roles</h1>
    
    
   <table class=data>
   <tr>
        <th class=data>Role</th><th class=data>Comment</th><th class=data>Edit</th>
   </tr>
   <m:iterate-collection collection="<%=roles%>">
   <tr class="data #?alt#">
        <td>#:getName#</td>
        <td>#:getComm#</td>
        <td><a href="Controller?workflow=EditRole&rid=#:getRid#"><m:img name="edit" title="Edit role"/></a></td>
   </tr>
   </m:iterate-collection>
   </table>
    
    <%--
        Example to get a property
        <jsp:getProperty name="ind" property="iid"/>
    --%>
    
    </body>
</html>
