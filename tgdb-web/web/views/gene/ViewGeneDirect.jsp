<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="gene" scope="request" type="org.tgdb.model.modelmanager.GeneDTO" />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title><jsp:getProperty name="gene" property="name"/> Gene Page</title>
    </head>
    <body>
    <table width="1000"  border="0" cellpadding="0" cellspacing="0">
        <tr valign="top">
            <td width="996" height="100" background="images/top_pic.jpg">
            <table><tr valign="bottom">
                <td width="270"></td>
                <td width="500" valign="bottom" height="96"></td>
                <td width="228"></td>
            </tr></table>
            </td>
            <td height="100">&nbsp;</td>
        </tr>
    </table>

    <h1>Gene <a href="DirectView?workflow=ViewGeneDirect&eid=<%=request.getParameter("eid")%>&gaid=<%=gene.getGaid()%>&gene.info=true"><m:img name="info"/></a></h1>


    <m:hide-block name="gene.info">
        <table class="info">
            <td>
                <b>Help & Info on this page:</b><br><br>
                <b>Gene Name:</b> Stands for the name of the gene.<br>
                <b>MGI Gene ID:</b> The ID of the gene as assigned by the MGI.<br>
                <b>Gene Symbol:</b> The symbol used to represent the gene. <br>
                <b>Gene Expression:</b> The gene's expression.<br>
                <b>Comments:</b> Comments relevant to the gene as typed by the user who submitted this gene. <br>
                <b>GENE Database Link:</b> Shows the GENE database id and is functional as a link to the GENE database, hence clicking on it will open a new window with all the information that is stored in the GENE database and is relevant to the gene.<br>
                <b>ENSEMBL Database Link:</b> Shows the ENSEMBL database id of the gene and works as link to it as well.<br>
                <b>Connected Mice: </b> To unfold this tab click on its title. Once unfolded it displays the list of mutant strains that are connected to this gene. Each strain's name and the comments that accompany it are listed. Clicking on the lens icon will redirect you to the detailed information page for the specific strain.<br><br>
                <m:hide privilege="MODEL_W" pid="<%=gene.getPid()%>">
                    <b>Editing this gene:</b> To edit this gene click on the edit icon on the gene information tab. The "Edit Gene" will open. There you can freely edit this gene.<br>
                    <b>Deleting this gene:</b> To remove this gene from the database you must click on the remove icon (the red x icon on the gene information tab). You will be asked to confirm your intention to delete the gene and if you click ok it will be deleted. Otherwise the gene will remain stored in the database. NOTE!!! You do not have to delete a gene to unassigned it from a mutant strain. To do that you have to visit the detailed information page of the strain and unassign the gene from there. If you delete a gene from this page it will be permanently deleted from the database!!!.<br>
                </m:hide>
            </td>
        </table>
        <br>
    </m:hide-block>
    
    <form action="DirectView" method="post">
        <input type="hidden" name="gaid" value='<jsp:getProperty name="gene" property="gaid"/>'/>
        <table class="block">
            <th class="block" colspan="2">
            </th>
            <tr class="block">
                <td class="block">
                    <b>Gene Name</b><br> <jsp:getProperty name="gene" property="name"/>
                </td>
            </tr>
            <tr class="block">
                <td class="block">
                    <b>Chromosome</b><br><jsp:getProperty name="gene" property="chromoName"/>
                </td>
            </tr>
            <tr class="block">
                <td class="block">
                    <b>MGI Gene ID</b><br> <jsp:getProperty name="gene" property="mgiid"/>
                </td>
            </tr>
            <tr class="block">
                <td class="block">
                    <b>Gene Symbol</b><br> <jsp:getProperty name="gene" property="genesymbol"/>
                </td>            
            </tr>
            <tr class="block">
                <td class="block">
                    <b>Gene Expression</b><br> <jsp:getProperty name="gene" property="geneexpress"/>
                </td>            
            </tr>
            <tr class="block">
                <td class="block">
                    <b>GENE Database Link</b>
                    <br>
                    <% if(gene.getIdgene().compareTo("not provided yet") != 0){ %>
                    <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=full_report&list_uids=<jsp:getProperty name="gene" property="idgene"/>" target="_blank">
                    <% } %>
                    <jsp:getProperty name="gene" property="idgene"/>
                    <% if(gene.getIdgene().compareTo("not provided yet") != 0){ %>
                    </a>
                    <% } %>
                </td>            
            </tr>
            <tr class="block">
                <td class="block">
                    <b>ENSEMBL Database Link</b>
                    <br>
                    <% if(gene.getIdensembl().compareTo("not provided yet") != 0){
                        if(gene.getIdensembl().contains("ENSMUSG")){
                    %>
                    <a href="http://www.ensembl.org/Mus_musculus/geneview?gene=<jsp:getProperty name="gene" property="idensembl"/>" target="_blank">
                    <% }
                        else{
                    %>
                            <a href="http://www.ensembl.org/Homo_sapiens/geneview?gene=<jsp:getProperty name="gene" property="idensembl"/>" target="_blank">
                    <%
                        }
                    }
                    %>
                    <jsp:getProperty name="gene" property="idensembl"/>
                    <% if(gene.getIdensembl().compareTo("not provided yet") != 0){ %>
                    </a>
                    <% } %>
                </td>            
            </tr>
            <tr class="block">
                <td class="block">
                    <b>Comment</b><br><jsp:getProperty name="gene" property="comm"/>
                </td>                
            </tr>
            
            <tr class="block">
                <td class="block">                
                    <b>Contact</b><br> <a href="mailto:<jsp:getProperty name="gene" property="userMail"/>" title="e-mail the contact person"><jsp:getProperty name="gene" property="userFullName"/></a> 
                </td>            
            </tr> 
            <tr class="block">
                <td class="block">
                    <b>Last updated</b><br> <jsp:getProperty name="gene" property="ts"/>
                </td>            
            </tr>      
        </table>
        <br>
        <table class="block">
            <th class="block">            
                <a href="DirectView?workflow=ViewGeneDirect&eid=<%=request.getParameter("eid")%>&gaid=<jsp:getProperty name="gene" property="gaid"/>&gene.models_display=true" title="Expand/Collapse this section">Connected Mice</a>
            </th>
            <tr>
            <td>
                <m:hide-block name="gene.models_display">
                    <table  class="block_data">
                        <tr>
                            <th class="data" width="45%">Model Name</th>
                            <th class="data" width="45%">Comment</th>
                            <th class="data" width="10%">Details</th>
                        </tr>
                        <m:iterate-collection collection="models">
                            <tr class="#?alt#">
                                <td>#:getLineName#</td>
                                <td>#:getComm#</td>
                                <td><a href="DirectView?workflow=ViewModelDirect&eid=#:getEid#" title="Get details for a model"><m:img name="view"/></a></td>
                            </tr>
                        </m:iterate-collection>         
                    </table>  
                </m:hide-block>
            </td>
            </tr>
        </table>
        
        <p>
        <% if(request.getParameter("eid")!=null){ %>
            <a href="DirectView?workflow=ViewModelDirect&eid=<%=request.getParameter("eid")%>" title="Go back"><m:img title="Go Back" name="back2models"/></a>&nbsp
        <% } %>    
        </p>
    </form>
    </body>
</html>

    
    </body>
</html>
