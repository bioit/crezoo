
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Collection" %>
<%
    org.tgdb.project.ParamDataObject pdo = (org.tgdb.project.ParamDataObject)request.getAttribute("paramdataobject");
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>View all files</title>
    </head>
    <body>     
        <h1>Files</h1>
        <form action="Controller" method="post">
        <table>
            <tr>
                <td>Status<br>
                    <select name="status">
                        <option value="checked">Checked</option>
                        <option value="imported">Imported</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan=3 align="right">                    
                    <input id="button" type="submit" name="collect.getfilesaction.display" value="Display">
                    <input id="button" type="submit" name="reset" value="Reset">
                </td>
            </tr>                                
        </table>       
        </form>
        
        <m:navigation-buttons workflow="ViewFiles"/>
        <table class="data">
        <tr>
            <th class="data">FileId</th>
            <th class="data">File Name</th>
            <th class="data">File Size</th>
            <th class="data">Comment</th>
            <th class="data">MimeType</th>
            <th class="data">FileType</th>
            <th class="data">Timestamp</th>
            <th class="data">View</th>
            <th class="data">Import</th>
            <th class="data">Delete</th>
        </tr>
        <m:iterate-collection collection="<%=(Collection)request.getAttribute("tmp.files")%>">
        <tr class="#?alt#">
            <td>#:getFileId#</td>
            <td>#:getName#</td>
            <td>#:getSize#</td>
            <td>#:getComm#</td>
            <td>#:getMimeType#</td>
            <td>#:getFileType#</td>
            <td>#:getTs#</td>
            <td><a href="Controller?workflow=ViewFile&fileid=#:getFileId#">View</a></td>
            <td><a href="Controller?workflow=ImportFile&fileid=#:getFileId#">Import</a></td>
            <td><a href="Controller?workflow=DeleteFile&fileid=#:getFileId#">Delete</a></td>
            
        </tr>
        
        </m:iterate-collection>
        </table> 
       <m:navigation-buttons workflow="ViewIndividuals"/>        
    </body>
</html>
