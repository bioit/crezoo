<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="modeldto" scope="request" type="org.tgdb.model.modelmanager.ExpModelDTO" />
<%
    org.tgdb.form.FormDataManager fdm = (org.tgdb.form.FormDataManager)request.getAttribute("formdata");

    org.tgdb.model.modelmanager.ExpModelDTO dto = (org.tgdb.model.modelmanager.ExpModelDTO)request.getAttribute("modeldto");
    java.util.Collection geneticBackground = (java.util.Collection)request.getAttribute("geneticBackground");
    int edithis = 0;
    String whoNow = (String)request.getAttribute("curruser");
    
    org.tgdb.TgDbCaller _caller = (org.tgdb.TgDbCaller)request.getSession().getAttribute("caller");
    
    if (modeldto.getContactName().compareTo(whoNow)==0 || _caller.hasPrivilege("PROJECT_ADM")){
        edithis = 13;
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; trangenic mouse</title>
	<link rel="stylesheet" type="text/css" href="css/crezoo-tabs.css" />
    <jsp:include page="/Header"/>
</head>
<body>
<div id="wrap">
	<div id="panel-left">
		<jsp:include page="/PanelLeft"/>
	</div>
	<div id="panel-right">
		<jsp:include page="/PanelRightTop"/>        
        <div id="panel-right-rest">
        <form action="Controller" method="post">
            <span class="header_01"><jsp:getProperty name="modeldto" property="lineName"/>&nbsp;&mdash;&nbsp;<jsp:getProperty name="modeldto" property="accNr"/></span>
            <table>
                <tr>
                    <td>
                        <p class="navtext">
                            <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                            <% if (edithis==13){ %>
                                <a href="Controller?workflow=EditModel&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>">Edit</a>&nbsp;
                                <a href="Controller?workflow=RemoveModel&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" title="Delete Tg Mouse" onclick="return confirm('Delete Tg Mouse?')">Delete</a>
                            <% } %>
                            </m:hide>
                            <input type="submit" name="back" value="back" title="Back" />
                        </p>
                    </td>
                </tr>
            </table>
        
        <%--menu--%>
        <table width="100%">
        <tr><td valign="top">
        <m:tabmenu name="general:genomics:documents" title="General:Gene and Allele Information:Documents" workflow="ViewModel"/>
        </td></tr>
        <tr>
        <td>
        <table class="block2" cellpadding="0" cellspacing="0">
            
            <tr><td colspan="8">
            <input type="hidden" name="eid" value='<jsp:getProperty name="modeldto" property="eid"/>'/>
            
            <%--general tab--%>
            <m:tab name="general">
            <body>
            <m:window title="Inducible" name="model.block.inducible" workflow="ViewModel">
				<menu></menu>
                <body>
                    <table class="block_data">
                        <tr><td><%=modeldto.getInducible()%></td></tr>
                    </table>
                </body>
            </m:window>
            <m:window title="Donating Investigator" name="model.block.donating_investigator" workflow="ViewModel">
				<menu></menu>
                <body>
                    <table class="block_data">
                        <tr><td><%=modeldto.getDonating_investigator()%></td></tr>
                    </table>
                </body>
            </m:window>
            <m:window title="Former Names" name="model.block.former_names" workflow="ViewModel">
				<menu></menu>
                <body>
                    <table class="block_data">
                        <tr><td><%=modeldto.getFormer_names()%></td></tr>
                    </table>
                </body>
            </m:window>
            <!--table for availability-->
            <m:window title="Availability" name="model.block.availability" workflow="ViewModel">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="Controller?workflow=AssignAvailability&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">Add</a>
                    <% } %>    
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="20%">Repository</th>
                            <th class="data" width="30%">Available Genetic Background</th>
                            <th class="data" width="20%">Strain State</th>
                            <th class="data" width="20%">Strain Type</th>
                            <th class="data" width="10%">&nbsp;</th>
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
                                <a href="Controller?workflow=UnAssignAvailability&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>&amp;rid=#:getRid#&amp;aid=#:getAid#&amp;stateid=#:getStateid#&amp;typeid=#:getTypeid#" onclick="return confirm('Remove Availability?')" class="navtext">delete</a>
                                <% } %>
                                </m:hide>
                                </td>
                            </tr>
                        </m:iterate-collection>              
                    </table>
                </body>
            </m:window>
  
            <br/>
            <!--table for genetic background info-->
            <m:window title="Genetic Background" name="model.block.genback" workflow="ViewModel">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                  <% if (edithis==13){ %>
                  <%  if (geneticBackground.isEmpty()== true){ %>
                    <a href="Controller?workflow=CreateGeneticBackground&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add</a>
                  <% } else {%>
                    <a href="Controller?workflow=EditGeneticBackground&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">edit</a>
                  <% } %>
                  <% } %>
               </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr>                        
                            <th class="data" width="18%">DNA Origin</th>
                            <th class="data" width="18%">Targeted Background</th>
                            <th class="data" width="18%">Host Background</th>
                            <th class="data" width="18%">Backcrossing Strain</th>
                            <th class="data" width="18%">Number Of Backcrosses</th>
							<th class="data" width="10%">&nbsp;</th>
                        </tr>
                        <m:iterate-collection collection="geneticBackground">
                            <tr class="#?alt#">
                                <td>#:getDna_origin_name#</td>
                                <td>#:getTargeted_back_name#</td>
                                <td>#:getHost_back_name#</td>
                                <td>#:getBackcrossing_strain_name#</td>
                                <td>#:getBackcrosses#</td>
								<td>&nbsp;</td>
                            </tr>
                        </m:iterate-collection>              
                    </table>
                </body>
            </m:window>
            
            <br/>
            <!--table for strain info (designation, mgi id, state, type)-->
            <m:window title="Strain Information" name="model.block.strain" workflow="ViewModel">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="Controller?workflow=AssignStrain" class="navtext">Add</a>
                    <% } %>
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="45%">Strain Name</th>
                            <th class="data" width="45%">ID</th>
                            <th class="data" width="10%">&nbsp;</th>
                        </tr>
                        <m:iterate-collection collection="strains">
                        <tr class="#?alt#">
                            <td>#:getDesignation#</td>
                            <td>#:getStrain_links_string#</td>
                            <td>
                                <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                <% if (edithis==13){ %>
                                <a href="Controller?workflow=EditStrain&amp;strainid=#:getStrainId#" class="navtext">edit</a>
                                <br/>
                                <a href="Controller?workflow=UnassignStrain&amp;strainid=#:getStrainId#" onclick="return confirm('Unassign Strain?')" class="navtext">unassign</a>
                                <% } %>
                                </m:hide>
                            </td>
                        </tr>
                        </m:iterate-collection>
                    </table>
                </body>
            </m:window>
            
            <br/>
            <!--table for author's comments-->
            <m:window title="Additional Comments" name="model.block.authcomm" workflow="ViewModel">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="Controller?workflow=EditAuthorsComments&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">edit</a>
                    <% } %>
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="100%">&nbsp;</th>
                        </tr>
                        <tr class="#?alt#">
                            <td><jsp:getProperty name="modeldto" property="comm"/></td>
                        </tr>
                    </table>    
                </body>
            </m:window>
            
            
            </body>
            </m:tab>
            
            <%--genomics tab--%>
            <m:tab name="genomics">
            <body>
            
            <!--table for transgene -->
            <m:window title="Transgene Information" name="model.block.geneaffected" workflow="ViewModel">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="Controller?workflow=AssignGeneModel" class="navtext">add</a>
                    <% } %>
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr>                        
                            <th class="data" width="40%">Symbol</th>
                            <th class="data" width="40%">Name</th>
                            <th class="data" width="10%">Chromosome</th>
                            <th class="data" width="10%">&nbsp;</th>
                        </tr>
                        <m:iterate-collection collection="transgenes">
                            <tr class="#?alt#">
                                <td><a href="Controller?workflow=ViewGene&amp;gaid=#:getGaid#" title="View Transgene" class="data_link">#:getGenesymbol#</a></td>
                                <td>#:getName#</td>
                                <td>#:getChromoName#</td>
                                <td>
                                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                        <% if (edithis == 13) {%>
                                        <a href="Controller?workflow=UnAssignGeneModel&amp;gaid=#:getGaid#" onclick="return confirm('Unassign Transgene?')" class="navtext">unassign</a>
                                        <% }%>
                                    </m:hide>
                                </td>
                            </tr>
                        </m:iterate-collection>              
                    </table>    
                </body>
            </m:window>
            <br/>

            <!--table for promoters-->
            <m:window title="Promoters" name="model.block.promoters" workflow="ViewModel">
                <menu></menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="40%">Symbol</th>
                            <th class="data" width="40%">Name</th>
                            <th class="data" width="10%">Chromosome</th>
                            <th class="data" width="10%">&nbsp;</th>
                        </tr>
                        <m:iterate-collection collection="promoters">
                            <tr class="#?alt#">
                                <td><a href="Controller?workflow=ViewPromoter&amp;gid=#:getGaid#" title="View Promoter" class="data_link">#:getGenesymbol#</a></td>
                                <td>#:getName#</td>
                                <td>#:getChromoName#</td>
                                <td>
                                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                    <% if (edithis==13){ %>
                                        <a href="Controller?workflow=EditPromoter&amp;gaid=#:getGaid#" class="navtext">edit</a>&nbsp;
                                        <br/>
                                        <a href="Controller?workflow=RemoveGene&amp;gaid=#:getGaid#" title="Delete Promoter" onclick="return confirm('Delete Promoter Completely?')" class="navtext">delete</a>
                                    <% } %>
                                    </m:hide>
                                </td>
                            </tr>
                        </m:iterate-collection>
                    </table>
                </body>
            </m:window>

            <br/>

            <!--table for expressed genes-->
            <m:window title="Expressed Genes" name="model.block.expressed_genes" workflow="ViewModel">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="Controller?workflow=AssignExpressedGene" class="navtext">add</a>
                    <% } %>
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="40%">Symbol</th>
                            <th class="data" width="40%">Name</th>
                            <th class="data" width="10%">Chromosome</th>
                            <th class="data" width="10%">&nbsp;</th>
                        </tr>
                        <m:iterate-collection collection="expressed_genes">
                            <tr class="#?alt#">
                                <td><a href="Controller?workflow=ViewExpressedGene&amp;gid=#:getGaid#" title="View Expressed Gene" class="data_link">#:getGenesymbol#</a></td>
                                <td>#:getName#</td>
                                <td>#:getChromoName#</td>
                                <td>
                                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                    <% if (edithis==13){ %>
                                        <a href="Controller?workflow=EditExpressedGene&amp;gaid=#:getGaid#" class="navtext">edit</a>&nbsp;
                                        <br/>
                                        <a href="Controller?workflow=RemoveGene&amp;gaid=#:getGaid#" title="Delete Expressed Gene" onclick="return confirm('Delete Expressed Gene Completely?')" class="navtext">delete</a>
                                        <br/>
                                        <a href="Controller?workflow=UnAssignGeneModel&amp;gaid=#:getGaid#" onclick="return confirm('Unassign Expressed Gene?')" class="navtext">unassign</a>
                                    <% } %>
                                    </m:hide>
                                </td>
                            </tr>
                        </m:iterate-collection>
                    </table>
                </body>
            </m:window>

            <br/>
            <!--table for allele + mutation info -->
            <m:window title="Allele Information" name="model.block.allele" workflow="ViewModel">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a class="navtext" href="Controller?workflow=AssignStrainAllele">add</a>
                    <% } %>    
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="10%">Allele Symbol</th>
                            <th class="data" width="30%">Allele Name</th>
                            <th class="data" width="15%">MGI ID</th>
                            <th class="data" width="25%">Allele Type</th>
                            <th class="data" width="10%">Attributes</th>
                            <th class="data" width="10%">&nbsp;</th>
                        </tr>
                        <m:iterate-collection collection="strainalleles"> 
                        <tr class="#?alt#">
                            <td>#:getSymbol#</td>
                            <td>#:getName#</td>
                            <td><a href="#:getMgi_url#" target="_blank">#:getMgi_id#</a></td>
                            <td>#:getMutations#</td>
                            <td>#:getAttributes#</td>
                            <td>
                            <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                            <% if (edithis==13){ %>
                            <a href="Controller?workflow=EditStrainAllele&amp;strainalleleid=#:getId#&amp;transgc=<%=modeldto.getDistParam()%>&amp;isatransgc=#:getIsStrainAlleleTransgenic#&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">edit</a>
                            <br/>
                            <a href="Controller?workflow=RemoveStrainAllele&amp;strainalleleid=#:getId#&amp;transgc=<%=modeldto.getDistParam()%>&amp;isatransgc=#:getIsStrainAlleleTransgenic#&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" onclick="return confirm('Remove Allele?')" class="navtext">delete</a>
                            <% } %>
                            </m:hide>
                            </td>
                        </tr>
                        </m:iterate-collection>
                    </table>
                </body>
            </m:window>
            
            <br/>
            <!--table for recombination efficiency -->
            <m:window title="Recombination Efficiency" name="model.1.block.receff" workflow="ViewModel" state="expanded">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                        <% if (edithis==13){ %>
                        <a href="Controller?workflow=EditRecombinationEfficiency&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">edit</a>
                        <% } %>
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                        <tr><th class="data">&nbsp;</th></tr>
                        <tr class="alternatingOne">
                            <td><jsp:getProperty name="modeldto" property="researchAppText"/></td>
                        </tr>
                    </table>
                </body>
            </m:window>
            
            <br/>
            <!--table for expression info-->
            <m:window title="Specificity" name="model.block.expression" workflow="ViewModel">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="Controller?workflow=CreateExpressionModel&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add</a>
                    <% } %>
                    </m:hide>  
                </menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="10%">Anatomy</th>
                            <th class="data" width="10%">Comment</th>
                            <th class="data" width="70%">Pix</th>
                            <th class="data" width="10%">&nbsp;</th>
                        </tr>
                        <m:iterate-collection collection="expressions">
                            <tr class="#?alt#">
                                <td>#:getExanatomy#</td>
                                <td>#:getExcomm#</td>
                                <td>#:getExfiletable#</td>
                                <td>
                                <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                <% if (edithis==13){ %>
                                <a href="Controller?workflow=EditExpressionModel&amp;exid=#:getExid#" class="navtext">edit</a>
                                <br/>
                                <a href="Controller?workflow=RemoveExpressionModel&amp;exid=#:getExid#" onclick="return confirm('Delete Specificity?')" class="navtext">delete</a>
                                <% } %>
                                </m:hide>
                                </td>
                            </tr>
                        </m:iterate-collection>
                    </table>
                </body>
            </m:window>
            
            <br/>
            <!--table for integration copy info-->
            <m:window title="Integration Site & Copy Number" name="model.block.integrationcopy" workflow="ViewModel">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="Controller?workflow=CreateIntegrationCopy&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add</a>
                    <% } %>
                    </m:hide>  
                </menu>
                <body>
                    <table class="block_data">
                        <tr>
                            <th class="data" width="45%">Integration Site</th>
                            <th class="data" width="45%">Copy Number</th>
                            <th class="data" width="10%">&nbsp;</th>
                        </tr>
                        <m:iterate-collection collection="ics">
                            <tr class="#?alt#">
                                <td>#:getIsite#</td>
                                <td>#:getCnumber#</td>
                                <td>
                                <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                <% if (edithis==13){ %>
								<a href="Controller?workflow=EditIntegrationCopy&amp;iscmid=#:getIscnid#" class="navtext">edit</a>
								<br/>
                                <a href="Controller?workflow=RemoveIntegrationCopy&amp;iscmid=#:getIscnid#" onclick="return confirm('Delete Integration Site & Copy Number?')" class="navtext">delete</a>
                                <% } %>
                                </m:hide>
                                </td>
                            </tr>
                        </m:iterate-collection>
                    </table>
                </body>
            </m:window>
            
            </body>
            </m:tab>
            
            <%--documents tab--%>
            <m:tab name="documents">
            <body>
            
            <!--table for handling & genotyping information-->
            <m:window title="Handling & Genotyping Instructions" name="model.block.res" workflow="ViewModel">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="Controller?workflow=CreateModelFileResource&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add file</a>
                        <a href="Controller?workflow=CreateModelLinkResource&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add link</a>    
                    <% } %>
                    </m:hide>
                </menu>
                <body>
                    <m:resource-simple-list resourceTreeCollection="resourceTree"/>
                </body>
            </m:window>
          
            <br/>
            <!--table for references-->
            <m:window title="References" name="model.block.ref" workflow="ViewModel">
                <menu>
                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                    <% if (edithis==13){ %>
                        <a href="Controller?workflow=CreateModelFileReference&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add file</a>
                        <a href="Controller?workflow=CreateModelLinkReference&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add link</a>
                    <% } %>
                    </m:hide>
                </menu>
                <body>
                    <table class="block_data">
                    <tr>                        
                        <th class="data" width="90%">Name</th>
                        <!--th class="data" width="10%">Type</th-->
                        <!--th class="data" width="30%">Comment</th-->
                        <th class="data" width="10%">&nbsp;</th>
                    </tr>
                    <m:iterate-collection collection="references">
                        <tr class="#?alt#">
                            <td>
                                <a href="#:getResource#" target="#:getTarget#" title="View file/Visit link" class="data_link">#:getName#</a>
                                <br/><br/>
				<i>#:getComm#</i>
                            </td>
                            <!--td>#:getType#</td-->
                            <!--td>#:getComm#</td-->
                            <td>
							<m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                            <% if (edithis==13){ %>
                                <a href="#:getEdit#&amp;refid=#:getRefid#" class="navtext">edit</a>
                                <br/>
                                <a href="Controller?workflow=RemoveModelReference&amp;refid=#:getRefid#" onclick="return confirm('Remove Reference?')" class="navtext">delete</a>
                            <% } %>
                            </m:hide>
							</td>
                        </tr>
                    </m:iterate-collection>              
                </table>
                </body>
            </m:window>
            
            </body>
            </m:tab>
        </td></tr>
        </table>
        </td></tr>
        </table>
        <!--some extra info-->
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <b>corresponding researcher: </b><a href="Controller?workflow=ViewUser&amp;id=<jsp:getProperty name="modeldto" property="contactId"/>" title="Contact Researcher"><jsp:getProperty name="modeldto" property="contactName"/></a>&nbsp;
                        <b>updated: </b><jsp:getProperty name="modeldto" property="ts"/>
                        <m:hide privilege="MODEL_ADM" suid="<%=modeldto.getSuid()%>">
                        &nbsp;<b>dissemination level: </b><jsp:getProperty name="modeldto" property="level"/>
                        </m:hide>
                    </p>
                </td>
            </tr>
        </table>
        </form>
        </div>
</div>
	<div id="clear-foot">&nbsp;</div>
</div>
<jsp:include page="/Foot"/>
</body>
</html>