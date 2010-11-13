<%@page contentType="text/html"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>Publications List File Upload</title>
        <script language="JavaScript" src="javascripts/validate.js"></script>
    </head>
    <body>
        <h1>load tg data</h1>    
        <form method=post enctype="multipart/form-data" action="Controller">
            <p>
                <table>       
                    <tr>                                  
                        <td>select file</td>
                    </tr>
                    <tr>
                        <td><input type="file" name="file"></td>
                    </tr>                                               
                    <tr>
                        <td>select repository</td>
                    </tr>
                    <tr><td>
                        <m:checkbox collection="repos" name="repo" idGetter="getRid" textGetter="getReponame"/>
                    </td></tr>
                    <tr>
                        <td align="right">
                            <input id="button" type="submit" name="upload" value="Upload" onClick="validate();return returnVal;">
                            <input id="button" type="submit" name="back" value="Cancel">  
                        </td>
                    </tr>                  
                </table>  
            </p>
        </form>
    </body>
</html>
