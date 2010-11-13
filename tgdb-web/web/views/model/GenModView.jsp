<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="genmoddto" scope="request" type="org.tgdb.model.modelmanager.GenModDTO" />
<%
    org.tgdb.form.FormDataManager fdm = (org.tgdb.form.FormDataManager)request.getAttribute("formdata");   
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>Edit group</title>   
    </head>
    <body>

    <h1>Genetic modification</h1>
    <table class="info">
        <td>
            This is the detailed page for a Gentic Modification entry. You can add several files and Gene Ontologies to 
            the Genetic Modification entry. Use the notepad/trashbin in the table listings to edit or remove a Genetin Modification. 
            Use the notepad/trashbin in the table listings to edit or remove a file or Gene Ontology.
        </td>
    </table>
    <form action="Controller" method="post">
        <p>
            <a href="Controller?workflow=ViewModels" title="Go to the listing over all models">Models</a>&nbsp>>
            <a href="Controller?workflow=ViewModel" title="Return to the current model">Model</a>&nbsp>>
            Genetic modification   
        </p>
        <table class="block">
            <th class="block" colspan="2">
                <a href="Controller?workflow=EditGenMod&gmid=<jsp:getProperty name="genmoddto" property="gmid"/>"><m:img name="edit" title="Edit this genetic modification"/></a>
                <a href="Controller?workflow=RemoveGenMod&gmid=<jsp:getProperty name="genmoddto" property="gmid"/>" onClick="return confirm('Remove genetic modification?')"><m:img name="delete" title="Remove this genetic modification"/></a>
            </th>
            <tr class="block">
                <td class="block">
                    <b>Modification name</b><br> <jsp:getProperty name="genmoddto" property="name"/>
                </td>
            </tr>
            <tr class="block">
                <td class="block">
                    <b>Comment</b><br> <jsp:getProperty name="genmoddto" property="comm"/>
                </td>            
            </tr>         
            <tr class="block">
                <td class="block">                
                    <b>User</b><br> <a href="Controller?workflow=ViewUser&id=<jsp:getProperty name="genmoddto" property="userId"/>" title="View the details for this user"><jsp:getProperty name="genmoddto" property="user"/></a> 
                </td>            
            </tr> 
            <tr class="block">
                <td class="block">
                    <b>Last updated</b><br> <jsp:getProperty name="genmoddto" property="ts"/>
                </td>            
            </tr>      
        </table>
        <br>
        <table class="block">
            <th class="block">            
                <a href="Controller?workflow=ViewGenMod&genmod.files_display=true" title="Expand/Collapse this section">Files</a>
                <m:hide privilege="MODEL_W">
                    <a href="Controller?workflow=GenModFileUpload&gmid=<jsp:getProperty name="genmoddto" property="gmid"/>"><m:img name="add" title="Add a file to this genetic modification"/></a>
                </m:hide>
            </th>
            <tr>
            <td>
                <m:hide-block name="genmod.files_display">
                    <p></p>
                    <table  class="block_data">
                    <tr>
                        <th class="data" width="20%">Name</th>
                        <th class="data" width="35%">Comment</th>
                        <th class="data" width="10%">User</th>
                        <th class="data" width="10%">Updated</th>
                        <m:hide privilege="FILE_W">
                            <th class="data" width="15%">Edit file info</th>
                            <th class="data" width="10%">Delete file</th>
                        </m:hide>
                    </tr>
                    <m:iterate-collection collection="files">
                        <tr class="#?alt#">
                            <td width="20%"><a href="Controller?workflow=ViewFile&fileid=#:getFileId#" title="View file">#:getName#</a></td>
                            <td width="35%">#:getComm#</td>
                            <td width="10%"><a href="Controller?workflow=ViewUser&id=#:getUser#" title="View the details for this user">#:getUser#</a></td>
                            <td width="10%">#:getTs#</td>                                
                            <m:hide privilege="FILE_W">
                                <td width="15%"><a href="Controller?workflow=EditGenModFile&fileid=#:getFileId#"><m:img name="edit" title="Edit the information regarding this file"/></a></td>
                                <td width="10%"><a href="Controller?workflow=RemoveGenModFile&fileid=#:getFileId#" onClick="return confirm('Remove file?')"><m:img name="delete" title="Remove this file"/></a></td>
                            </m:hide>
                        </tr>
                    </m:iterate-collection>     
                    </table>  
                </m:hide-block>
            </td>
            </tr>
        </table>
        <br>
        <table class="block">
            <th class="block">            
                <a href="Controller?workflow=ViewGenMod&genmod.go_display=true" title="Expand/Collapse this section">Gene ontology</a>
                <m:hide privilege="MODEL_W">
                    <a href="Controller?workflow=AddGeneOntology&gmid=<jsp:getProperty name="genmoddto" property="gmid"/>"><m:img name="add" title="Add a gene ontology to this genetic modification"/></a>
                </m:hide>
            </th>
            <tr>
            <td>
                <m:hide-block name="genmod.go_display">
                    <table  class="block_data">
                        <tr>
                            <th class="data" width="20%">Name</th>
                            <th class="data" width="40%">Comment</th>
                            <th class="data" width="10%">User</th>
                            <th class="data" width="10%">Updated</th>
                            <th class="data" width="10%">Edit</th>
                            <th class="data" width="10%">Remove</th>
                        </tr>
                        <m:iterate-collection collection="geneontologies">
                            <tr class="#?alt#">
                                <td width="20%"><a href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?query=#:getLinkid#" target="_blank"  title="Go to this entry in the GO database">#:getName#</a></td>
                                <td width="40%">#:getComm#</td>
                                <td width="10%"><a href="Controller?workflow=ViewUser&id=#:getUserId#" title="View the details for this user">#:getUser#</a></td>
                                <td width="10%">#:getTs#</td>
                                <td width="10%"><a href="Controller?workflow=EditGeneOntology&goid=#:getGoid#"><m:img name="edit" title="Edit this entry"/></a></td>
                                <td width="10%"><a href="Controller?workflow=RemoveGeneOntology&goid=#:getGoid#" onClick="return confirm('Remove ontology?')"><m:img name="delete" title="Remove this gene ontology"/></a></td>
                            </tr>
                        </m:iterate-collection>         
                    </table>  
                </m:hide-block>
            </td>
            </tr>
        </table>   
    </form>
    </body>
</html>
