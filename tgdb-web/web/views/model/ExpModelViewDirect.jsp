<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="modeldto" scope="request" type="org.tgdb.model.modelmanager.ExpModelDTO" />
<jsp:useBean id="straindto" scope="request" type="org.tgdb.model.modelmanager.StrainDTO" />

<%
    org.tgdb.form.FormDataManager fdm = (org.tgdb.form.FormDataManager)request.getAttribute("formdata");

    org.tgdb.model.modelmanager.ExpModelDTO dto = (org.tgdb.model.modelmanager.ExpModelDTO)request.getAttribute("modeldto");
    //int genotypingId = dto.getGenotypingId();
    //int handlingId = dto.getHandlingId();
    
    java.util.Collection geneticBackground = (java.util.Collection)request.getAttribute("geneticBackground");
    int edithis = 0;
    String whoNow = (String)request.getAttribute("curruser");
    
    if (modeldto.getContactName().compareTo(whoNow)==0 || whoNow.compareTo("MUGEN Admin")==0){
        edithis = 13;
    }
    
    String whatDVLevel = (String)request.getAttribute("DirectViewLevel");
/*
    String addHandling = "";
    String removeHandling = "Remove";

    String addGenotyping = "";
    String removeGenotyping = "Remove";

    if(genotypingId == 0)
    {
        addGenotyping = "Add";
        removeGenotyping = "";
    }
    if(handlingId == 0)
    {
        addHandling = "Add";
        removeHandling = "";
    }
*/
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title><jsp:getProperty name="modeldto" property="lineName"/> Strain Page</title>
    </head>
    <% if (whatDVLevel.compareTo("Public")==0){ %>
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
        
        <h1>
            <jsp:getProperty name="modeldto" property="lineName"/>  Strain Detailed Information 
        </h1>
  
        <p class="toolbar">
            <a href="DirectView?workflow=ViewModelDirect&eid=<%=modeldto.getEid()%>&modelinfo.info=true"><m:img name="info_24"/></a>
            <a href="DirectView?workflow=ViewModelDirect&eid=<%=modeldto.getEid()%>&expand_all=true&name_begins=model.block"><m:img title="Expand all boxes" name="nav_close_24"/></a>
            <a href="DirectView?workflow=ViewModelDirect&eid=<%=modeldto.getEid()%>&collapse_all=true&name_begins=model.block"><m:img title="Collapse all boxes" name="nav_open_24"/></a>
        </p>
        <!--info block - appears when clicking i button -->
        <m:hide-block name="modelinfo.info">
            <table class="info">
                <td>
                    <b>Help & Info on this page:</b><br><br>
                    This is the detailed page for a mouse. All information is grouped into 9 categories which are represented with 9 different tabs on this page. To reveal information of a tab you just need to click on the tab's title. To unfold all tabs click on the unfold icon right next to the blue Help & Info exclamation mark.<br>
                    <b>General Information:</b> Withholds basic information such as the common line name, the unique identification number assigned to the mouse, its research application type, the name of the contact person (which also works as a contact link), the institution in which the strain was created. Other information concern the date the strain data have been last updated/created and the user of the database that performed this update.<br>
                    The blue exclamation mark icon on this tab, once hit, will reveal the research application comments the user might have added to this mutant strain.<br>
                    <b>Availability:</b> Contains information on the mutant strains availability like in which repository it is available from, the available genetic background, the state and the type of the strain.<br>
                    <b>Genetic Background:</b> This tab contains information about the background of the mouse. The background the DNA derived from, the hosting & targeted background, the backcrossing strain and the number of backcrosses.<br>
                    <b>Strain Information:</b> In this category the official -according to MGNC's nomenclature rules- strain designation is provided and the MGI id of the strain if it exists.<br>
                    <b>Allele & mutations:</b> As the title suggests this tab contains information about the strains allele(s) and mutation. You can see the allele name and its symbol, the MGI id of the allele, the relevant gene the type of mutation of the mutant strain and an extra attribute that informs you if the mutation type is conditional, inducible, conditional+inducible or other.<br>
                    <b>Genes Affected:</b> This section withholds the name of the gene(s) affected for this mutant strain, comments relevant to the specific gene as added by a database user, the chromosome on which the gene is located and the user that performed the latest update on the gene and the date this update took place. Clicking on the lens (a.k.a. "detailed info") icon you are redirected to a new page with even more information about a specific gene. Note also that the gene's name on this tab works as a link to the GENE database, so clicking on it will open a new window containing the information that is stored in the GENE database about this gene.<br>
                    <b>Handling & Genotyping Instructions:</b> Here you will find handling and genotyping instructions in the form of documents that are stored in the database or in the form of links to web-pages or other on-line documents. The name is the actual name of the instruction file and serves as a link to the document whether it is available from a different server or the database server. The type attribute informs you of the type of the instruction file, the comment column simply contains comments provided by the user who submitted this instruction file, the user and date columns carry the name of the user who edited/provided the resource and the date he/she actualized this edit/submission.<br>
                    <b>References:</b> This tab contains all references for this mutant strain in the form of documents that are stored in the database or links to web-pages or other on-line documents. The name is the actual name of the reference title and serves as a link to the document whether it is available from a different server or from the database server. The type attribute informs you of the type of the reference file, the comment column simply contains comments provided by the user who submitted this reference, the user and date columns carry the name of the user who edited/provided the reference and the date this edit/submission was actualized.<br>
                    <b>Author's Comments:</b> Here, as the title suggests, you will find comments about this mutant strain.<br>
                </td>
            </table>
        </m:hide-block>
        
        <form action="Controller" method="post">
            <input type="hidden" name="eid" value='<jsp:getProperty name="modeldto" property="eid"/>'/>
            
            <!--table with basic info-->
            <m:window title="General Information" name="model.main" workflow="ViewModelDirect" state="expanded">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                        <% if (edithis==13){ %>
                        <a href="DirectView?workflow=EditModel&eid=<jsp:getProperty name="modeldto" property="eid"/>"><m:img name="edit" title="Edit this mutant"/></a>
                        <a href="DirectView?workflow=RemoveModel&eid=<jsp:getProperty name="modeldto" property="eid"/>" title="Delete this mutant" onClick="return confirm('Remove mutant? PLEASE NOTE: ALL INFORMATION SUCH AS FILES ETC. CONNECTED TO THE MUTANT WILL ALSO BE REMOVED!!!')"><m:img name="delete"/></a>
                        <% } %>
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">

                        <!--line with accession nr + line name-->
                        <tr class="block">
                            <td class="block">
                                <b>MMMDb ID</b><br> <jsp:getProperty name="modeldto" property="accNr"/>
                            </td>
                            <td class="block">
                                <b>Common Line name</b><br> <jsp:getProperty name="modeldto" property="lineName"/>
                            </td>
                            <td class="block">
                                <b>Research applications type</b><a href="DirectView?workflow=ViewModelDirect&eid=<%=modeldto.getEid()%>&model.block.rappcomm=true"> <m:img name="info"/></a><br><jsp:getProperty name="modeldto" property="researchAppType"/>
                            </td>
                        </tr>
                        <!--line with contact link + authors comment-->
                        <tr class="block">
                            <td class="block">
                                <b>Contact</b><br><a href="mailto:<jsp:getProperty name='modeldto' property='contactMail'/>"><jsp:getProperty name='modeldto' property='contactName'/></a>
                            </td>
                            <td class="block">
                                <b>Institution</b><br><jsp:getProperty name="modeldto" property="participant"/>
                            </td>
                            <m:hide-block name="model.block.rappcomm">
                                <td>
                                    <b>Research applications comments</b><br><jsp:getProperty name="modeldto" property="researchAppText"/>
                                </td>
                            </m:hide-block>              
                        </tr>
                        
                
                        <!--line with phenotypes only-->
                        <m:hide privilege="MODEL_ADM" suid="<%=modeldto.getSuid()%>">
                        <tr class="block">
                            <td>
                                <b>Phenotypes</b><br><a href="DirectView?workflow=ViewPhenotypes&_identity=<jsp:getProperty name="modeldto" property="accNr"/>" title="View the phenotypes for this model"/><jsp:getProperty name="modeldto" property="phenotypes"/></a>
                            </td>
                        </tr>
                        </m:hide>
                
                        <tr class="#?alt#">
                            <!--line with last updated timestamp-->
                            <tr class="block">
                                <td class="block">
                                    <b>last updated on: </b><jsp:getProperty name="modeldto" property="ts"/>
                                </td>
                                <td class="block">
                                <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                    <b>dissemination level </b><jsp:getProperty name="modeldto" property="level"/>
                                </m:hide>
                                </td>
                            </tr>
                        </tr>
                    </table>
                </body>
            </m:window>
  
            <br>
            
            <!--table for availability-->
            <m:window title="Availability" name="model.block.availability" workflow="ViewModelDirect">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="DirectView?workflow=AssignAvailability&eid=<jsp:getProperty name="modeldto" property="eid"/>"><m:img name="add" title="Add availability information for this model"/></a>
                    <% } %>    
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="15%">Repository</th>
                            <th class="data" width="30%">Available Genetic Background</th>
                            <th class="data" width="20%">Strain State</th>
                            <th class="data" width="20%">Strain Type</th>
                            <th class="data" width="10%">
                            <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                            <% if (edithis==13){ %>
                            Remove
                            <% } %> 
                            </m:hide>
                            </th>
                        </tr>
                        <m:iterate-collection collection="availability">
                            <tr class="#?alt#">
                                <td>#:getReponame#</td>
                                <td>#:getAvbackname#</td>
                                <td>#:getStatename#</td>
                                <td>#:getTypename#</td>    
                                <td>
                                <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                <% if (edithis==13){ %>
                                <a href="DirectView?workflow=UnAssignAvailability&eid=<jsp:getProperty name="modeldto" property="eid"/>&rid=#:getRid#&aid=#:getAid#&stateid=#:getStateid#&typeid=#:getTypeid#"><m:img name="delete" title="Remove this availability information line"/></a>
                                <% } %>
                                </m:hide>
                                </td>
                            </tr>
                        </m:iterate-collection>              
                    </table>
                </body>
            </m:window>   
  
            <br>

            <!--table for genetic background info-->
            <m:window title="Genetic Background" name="model.block.genback" workflow="ViewModelDirect">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                  <% if (edithis==13){ %>
                  <%  if (geneticBackground.isEmpty()== true){ %>
                    <a href="DirectView?workflow=CreateGeneticBackground&eid=<jsp:getProperty name="modeldto" property="eid"/>"><m:img name="add" title="Add genetic background information to this model"/></a>
                  <% } else {%>
                    <a href="DirectView?workflow=EditGeneticBackground&eid=<jsp:getProperty name="modeldto" property="eid"/>"><m:img name="edit" title="Edit genetic background information"/></a>
                  <% } %>
                  <% } %>
               </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr>                        
                            <th class="data" width="10%">DNA Origin</th>
                            <th class="data" width="10%">Targeted Background</th>
                            <th class="data" width="10%">Host Background</th>
                            <th class="data" width="10%">Backcrossing Strain</th>
                            <th class="data" width="10%">Number Of Backcrosses</th>
                        </tr>
                        <m:iterate-collection collection="geneticBackground">
                            <tr class="#?alt#">
                                <td>#:getDna_origin_name#</td>
                                <td>#:getTargeted_back_name#</td>
                                <td>#:getHost_back_name#</td>
                                <td>#:getBackcrossing_strain_name#</td>
                                <td>#:getBackcrosses#</td>
                            </tr>
                        </m:iterate-collection>              
                    </table>
                </body>
            </m:window>
                    
            <br>
            
            <!--table for strain info (designation, mgi id, state, type)-->
            <m:window title="Strain Information" name="model.block.strain" workflow="ViewModelDirect">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="DirectView?workflow=EditStrain&strainid=<jsp:getProperty name="straindto" property="strainId"/>"><m:img name="edit" title="Edit this strain"/></a>
                    <% } %>
                    </m:hide>  
                </menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="30%">Designation</th>
                            <th class="data" width="30%">MGI Id</th>
                        </tr>
                        <tr class="#?alt#">
                            <td><jsp:getProperty name="straindto" property="designation"/></td>
                            <td><jsp:getProperty name="straindto" property="mgi_id"/></td>
                        </tr>
                    </table>
                </body>
            </m:window>
            
            <br>
            <!--table for allele + mutation info -->
            <m:window title="Allele and mutations" name="model.block.allele" workflow="ViewModelDirect">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="DirectView?workflow=CreateStrainAllele&strainid=<jsp:getProperty name="straindto" property="strainId"/>"><m:img name="add" title="Add allele"/></a>
                    <% } %>    
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="20%">Symbol</th>
                            <th class="data" width="20%">Name</th>
                            <th class="data" width="20%">MGI Allele Acc Id</th>
                            <th class="data" width="20%">Gene Name</th>
                            <th class="data" width="20%">Mutations</th>
                            <th class="data" width="15%">Attributes</th>
                            <th class="data" width="15%">
                            <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                            <% if (edithis==13){ %>
                            Details
                            <% } %>
                            </m:hide>
                            </th>
                        </tr>
                        <m:iterate-collection collection="strainalleles"> 
                        <tr class="#?alt#">
                            <td>#:getSymbol#</td>
                            <td>#:getName#</td>
                            <td>#:getMgi_id#</td>
                            <td>#:getGeneName#</td>
                            <td>#:getMutations#</td>
                            <td>#:getAttributes#</td>
                            <td>
                            <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                            <% if (edithis==13){ %>
                            <a href="DirectView?workflow=EditStrainAllele&strainalleleid=#:getId#"><m:img name="edit"/></a>
                            <% } %>
                            </m:hide>
                            </td>
                        </tr>
                        </m:iterate-collection>
                    </table>
                </body>
            </m:window>
          
            <br>
            <!--table for genes affected-->
            <m:window title="Genes Affected" name="model.block.geneaffected" workflow="ViewModelDirect">
                <menu></menu>
                <body>
                    <table class="block_data">
                        <tr>                        
                            <th class="data" width="20%">Name</th>
                            <th class="data" width="20%">Comment</th>
                            <th class="data" width="20%">Chromosome</th>
                            <th class="data" width="10%">Updated</th>
                            <th class="data" width="10%">View</th>
                        </tr>
                        <m:iterate-collection collection="genesAffected">
                            <tr class="#?alt#">
                                <td><a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=search&db=gene&term=#:getName#" target="_blank" title="Lookup in Entrez Gene">#:getName#</a></td>
                                <td>#:getComm#</td>
                                <td>#:getChromoName#</td>
                                <td>#:getTs#</td>
                                <td><a href="DirectView?workflow=ViewGeneDirect&gaid=#:getGaid#&eid=<jsp:getProperty name="modeldto" property="eid"/>"><m:img name="view" title="View the entry"/></a></td>
                            </tr>
                        </m:iterate-collection>              
                    </table>    
                </body>
            </m:window>
            
            <br>
            
            <m:window title="Handling & Genotyping Instructions" name="model.block.res" workflow="ViewModelDirect">
                <menu></menu>
                <body>

                    <m:resource-simple-list resourceTreeCollection="resourceTree"/>
                    
                </body>
            </m:window>
            
            
          
            <br>
            
            <!--table for references-->
            <m:window title="References" name="model.block.ref" workflow="ViewModelDirect">
                <menu></menu>
                <body>
                    <table class="block_data">
                    <tr>                        
                        <th class="data" width="20%">Name</th>
                        <th class="data" width="10%">Type</th>
                        <th class="data" width="30%">Comment</th>
                        <th class="data" width="10%">Updated</th>
                    </tr>
                    <m:iterate-collection collection="references">
                        <tr class="#?alt#">
                            <td><a href="#:getResource#" target="#:getTarget#" title="View file/Visit link">#:getName#</a></td>
                            <td>#:getType#</td>
                            <td>#:getComm#</td>
                            <td>#:getTs#</td>
                        </tr>
                    </m:iterate-collection>              
                </table>
                </body>
            </m:window>
            
            
            
  
            <br>
            <!--table for author's comments-->
            
            <m:window title="Author's Comments" name="model.block.authcomm" workflow="ViewModelDirect">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="DirectView?workflow=EditModel&eid=<jsp:getProperty name="modeldto" property="eid"/>"><m:img name="edit" title="Edit Author's Comments"/></a>
                    <% } %>
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="100%">A few words about  <jsp:getProperty name="modeldto" property="lineName"/></th>
                        </tr>
                        <tr class="#?alt#">
                            <td><jsp:getProperty name="modeldto" property="comm"/></td>
                        </tr>
                    </table>    
                </body>
            </m:window>
        </form>
    </body>
    <% } else { %>
    <%--<body>
    <div align="center">
    <table>
    <tr><td><img src="images/stop.jpg"></td></tr>
    </table>
    </div>
    </body>--%>
  <body>
  
  
  <div align="center">
    <h1>NO ADMISSION TO RESOURCE</h1>
    <p>YOU DON'T HAVE PERMISSION TO ACCESS THE RESOURCE YOU REQUESTED</p>
      
    <table>
    <tr><td><img src="images/stop.jpg"></td></tr>
    </table>
    
  </div>
  
  </body>
    <% } %>
</html>
