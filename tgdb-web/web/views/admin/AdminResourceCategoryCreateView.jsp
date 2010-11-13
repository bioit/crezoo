<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>         
        <title>Create Resource Category</title>
    </head>
    <body>
        <h1>Create Resource Category</h1>
        <form action="Controller" method="post">
            <p>
                <input type="hidden" name="pid" value='<%=request.getAttribute("pid")%>'/>
                <table>
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
            <p>
                <input id="button" type="submit" name="create" value="Create" onClick="validate();return returnVal;">                
                <input id="button" type="submit" name="back" value="Back">
            </p>
        </form>   
    </body>
</html>
