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

        <h1>Edit research application</h1>
    
        <form action="Controller" method="post">
            <table>
                <tr>
                    <td>
                        <b>Name</b><br><input type="text" name="name" value="<jsp:getProperty name="rapp" property="name"/>">
                    </td>
                </tr>
                <tr>
                    <td>
                        <b>Comment</b><br><textarea name="comm" rows="6" cols=40><jsp:getProperty name="rapp" property="comm"/></textarea> 
                    </td>            
                </tr>  
            </table>
            <m:save saveName="save"/>
        </form>
    </body>
</html>
