<%@page contentType="text/html"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>
        <table>
            <tr valign="bottom">
                <td class="head">Resource File Upload</td>
            </tr>
        </table>
        <hr id="ruler">
        <form method=post enctype="multipart/form-data" action="Controller">
            <input type="hidden" name="id" value='<%=(String)request.getParameter("id")%>'/>
            <input type="hidden" name="catId" value='<%=(String)request.getParameter("catId")%>'/>
            <p>
                <table>       
                    <tr>                                  
                        <td>File</td>
                    </tr>
                    <tr>
                        <td><input type="file" name="file"></td>
                    </tr>  
                     <tr>                                  
                        <td>Name</td>
                    </tr>
                    <tr>
                        <td><input type="text" cols="35" name="name"></td>
                    </tr>  
                     <tr>
                        <td>Category</td>
                    </tr>
                    <tr>
                        <td><m:checkbox name="catid" collection="categories" idGetter="getCatId" textGetter="getCatName"/></td>
                    </tr>
                    
                    <tr>
                        <td>Comment</td>
                    </tr>
                    <tr>
                        <td><textarea type="text" cols="35" rows="6" name="comm"></textarea></td>
                    </tr>                                               
                    <tr>
                        <td>&nbsp</td>
                    </tr>
                    <tr>
                        <td align="right">
                            <input id="button" type="submit" name="upload" value="Upload">                        
                            <input id="button" type="submit" name="back" value="Cancel">  
                        </td>
                    </tr>                  
                </table>  
            </p>
        </form>
    </body>
</html>
