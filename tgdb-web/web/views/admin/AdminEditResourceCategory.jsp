<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>         
        <title>Edit Resource Category</title>
    </head>
    <body>
        <jsp:useBean id="category" scope="request" type="org.tgdb.resource.resourcemanager.ResourceCategoryDTO" />
        <h1>Edit Resource Category</h1>
        <form action="Controller" method="post">
            <p>
                <input type="hidden" name="catId" value='<jsp:getProperty name='category' property='catId'/>'/>
                <input type="hidden" name="pid" value='<jsp:getProperty name='category' property='projectId'/>'/>
                <table>
                    <tr>                    
                        <td>Name</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="name" size="35" value='<jsp:getProperty name='category' property='catName'/>'/></td>
                    </tr>  
                    <tr>                    
                        <td>Comment</td>
                    </tr>
                    <tr>
                        <td><textarea type="text" cols="35" rows="6" name="comm"><jsp:getProperty name='category' property='comm'/></textarea></td>
                    </tr>                     
                </table>
            </p>
            <p>
                <m:save saveName="save" cancelName="back"/>
            </p>
        </form>   
    </body>
</html>
