<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="modeldto" scope="request" type="org.tgdb.model.modelmanager.ExpModelDTO" />
<%
            org.tgdb.form.FormDataManager fdm = (org.tgdb.form.FormDataManager) request.getAttribute("formdata");

            org.tgdb.model.modelmanager.ExpModelDTO dto = (org.tgdb.model.modelmanager.ExpModelDTO) request.getAttribute("modeldto");
            java.util.Collection geneticBackground = (java.util.Collection) request.getAttribute("geneticBackground");
            int edithis = 0;
            String whoNow = (String) request.getAttribute("curruser");

            org.tgdb.TgDbCaller _caller = (org.tgdb.TgDbCaller) request.getSession().getAttribute("caller");

            if (modeldto.getContactName().compareTo(whoNow) == 0 || _caller.hasPrivilege("PROJECT_ADM")) {
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
                        <input type="hidden" name="eid" value='<jsp:getProperty name="modeldto" property="eid"/>'/>
                        <span class="header_01"><jsp:getProperty name="modeldto" property="lineName_ss"/>&nbsp;&mdash;&nbsp;<jsp:getProperty name="modeldto" property="accNr"/></span>

                        <p class="navtext">
                            <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                <% if (edithis == 13) {%>
                                <a href="Controller?workflow=EditModel&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>">Edit</a>&nbsp;
                                <a href="Controller?workflow=RemoveModel&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" title="Delete Tg Mouse" onclick="return confirm('Delete Tg Mouse?')">Delete</a>
                                <% }%>
                            </m:hide>
                            <input type="submit" name="back" value="back" title="Back" />
                        </p>
                        <div id="tabs">
                            <ul>
                                <li><a href="#fragment-1"><span>General</span></a></li>
                                <li><a href="#fragment-2"><span>Gene &amp; Allele Information</span></a></li>
                                <li><a href="#fragment-3"><span>Documents</span></a></li>
                            </ul>
                            <div id="fragment-1">
                                <p>
			Inducible: <%=modeldto.getInducible()%> / Donating Investigator: <%=modeldto.getDonating_investigator()%> / Former Names: <%=modeldto.getFormer_names()%>
                                </p>
                                <!--subsection for Availability-->
                                <div class="subsection">
                                    <span>
						Availability
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a href="Controller?workflow=AssignAvailability&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">Add</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
                                    <table class="block_data">
                                        <!--tr>
                                            <th class="data" width="20%">Repository</th>
                                            <th class="data" width="20%">Strain Designation</th>
                                            <th class="data" width="20%">Available Genetic Background</th>
                                            <th class="data" width="20%">Strain State</th>
                                            <th class="data" width="10%">Strain Type</th>
                                            <th class="data" width="10%">&nbsp;</th>
                                        </tr-->
                                        <m:iterate-collection collection="availability">
                                            <tr class="#?alt#">
                                                <!--td>#:getReponame#</td>
                                                <td>#:getStraindesignation_ss#</td>
                                                <td>#:getAvbackname#</td>
                                                <td>#:getStatename#</td>
                                                <td>#:getTypename#</td-->
                                                <td width="90%">
                                                    <i style="margin-left: 20px; font-weight:bold">Repository:</i> #:getReponame#
                                                    <hr/>
                                                    <i style="margin-left: 20px; font-weight:bold">Strain Designation:</i> #:getStraindesignation_ss#
                                                    <hr/>
                                                    <i style="margin-left: 20px; font-weight:bold">Available Genetic Background:</i> #:getAvbackname#
                                                    <hr/>
                                                    <i style="margin-left: 20px; font-weight:bold">Strain State:</i> #:getStatename#
                                                    <hr/>
                                                    <i style="margin-left: 20px; font-weight:bold">Strain Type:</i> #:getTypename#
                                                </td>
                                                <td>
                                                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                                        <% if (edithis == 13) {%>
                                                        <a href="Controller?workflow=UnAssignAvailability&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>&amp;rid=#:getRid#&amp;aid=#:getAid#&amp;stateid=#:getStateid#&amp;typeid=#:getTypeid#&amp;strainid=#:getStrainid#" onclick="return confirm('Remove Availability?')" class="navtext">delete</a>
                                                        <% }%>
                                                    </m:hide>
                                                </td>
                                            </tr>
                                        </m:iterate-collection>
                                    </table>
                                </div>
                                <!--subsection for Genetic Background-->
                                <div class="subsection">
                                    <span>
			Genetic Background
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <%  if (geneticBackground.isEmpty() == true) {%>
                                            <a href="Controller?workflow=CreateGeneticBackground&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add</a>
                                            <% } else {%>
                                            <a href="Controller?workflow=EditGeneticBackground&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">edit</a>
                                            <% }%>
                                            <% }%>
                                        </m:hide>
                                    </span>
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
                                </div>
                                <!--subsection for Strain Information-->
                                <div class="subsection">
                                    <span>
			Strain Information
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a href="Controller?workflow=AssignStrain" class="navtext">Add</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
                                    <table class="block_data">
                                        <tr>
                                            <th class="data" width="45%">Strain Name</th>
                                            <th class="data" width="45%">ID</th>
                                            <th class="data" width="10%">&nbsp;</th>
                                        </tr>
                                        <m:iterate-collection collection="strains">
                                            <tr class="#?alt#">
                                                <td>#:getDesignation_ss#</td>
                                                <td>#:getStrain_links_string#</td>
                                                <td>
                                                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                                        <% if (edithis == 13) {%>
                                                        <a href="Controller?workflow=EditStrain&amp;strainid=#:getStrainId#" class="navtext">edit</a>
                                                        <br/>
                                                        <a href="Controller?workflow=UnassignStrain&amp;strainid=#:getStrainId#" onclick="return confirm('Unassign Strain?')" class="navtext">unassign</a>
                                                        <% }%>
                                                    </m:hide>
                                                </td>
                                            </tr>
                                        </m:iterate-collection>
                                    </table>
                                </div>
                                <!--subsection for Additional Comments-->
                                <div class="subsection">
                                    <span>
			Additional Comments
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a href="Controller?workflow=EditAuthorsComments&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">edit</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
                                    <table class="block_data">
                                        <tr class="alternatingOne">
                                            <td><jsp:getProperty name="modeldto" property="comm"/></td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                            <div id="fragment-2">
                                <p></p>
                                <!--subsection for Transgene Information-->
                                <div class="subsection">
                                    <span>
			Transgene Information
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a href="Controller?workflow=AssignGeneModel" class="navtext">add</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
                                    <table class="block_data">
                                        <tr>
                                            <th class="data" width="40%">Symbol</th>
                                            <th class="data" width="40%">Name</th>
                                            <th class="data" width="10%">Chromosome</th>
                                            <th class="data" width="10%">&nbsp;</th>
                                        </tr>
                                        <m:iterate-collection collection="transgenes">
                                            <tr class="#?alt#">
                                                <td><a href="Controller?workflow=ViewGene&amp;gaid=#:getGaid#" title="View Transgene" class="data_link">#:getGenesymbol_ss#</a></td>
                                                <td>#:getName_ss#</td>
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
                                </div>
                                <!--subsection for Promoters-->
                                <div class="subsection">
                                    <span>
			Promoters
                                    </span>
                                    <table class="block_data">
                                        <tr>
                                            <th class="data" width="40%">Symbol</th>
                                            <th class="data" width="40%">Name</th>
                                            <th class="data" width="10%">Chromosome</th>
                                            <th class="data" width="10%">&nbsp;</th>
                                        </tr>
                                        <m:iterate-collection collection="promoters">
                                            <tr class="#?alt#">
                                                <td><a href="Controller?workflow=ViewPromoter&amp;gid=#:getGaid#" title="View Promoter" class="data_link">#:getGenesymbol_ss#</a></td>
                                                <td>#:getName_ss#</td>
                                                <td>#:getChromoName#</td>
                                                <td>
                                                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                                        <% if (edithis == 13) {%>
                                                        <a href="Controller?workflow=EditPromoter&amp;gaid=#:getGaid#" class="navtext">edit</a>&nbsp;
                                                        <br/>
                                                        <a href="Controller?workflow=RemoveGene&amp;gaid=#:getGaid#" title="Delete Promoter" onclick="return confirm('Delete Promoter Completely?')" class="navtext">delete</a>
                                                        <% }%>
                                                    </m:hide>
                                                </td>
                                            </tr>
                                        </m:iterate-collection>
                                    </table>
                                </div>
                                <!--subsection for Expressed Genes-->
                                <div class="subsection">
                                    <span>
			Expressed Genes
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a href="Controller?workflow=AssignExpressedGene" class="navtext">add</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
                                    <table class="block_data">
                                        <tr>
                                            <th class="data" width="40%">Symbol</th>
                                            <th class="data" width="40%">Name</th>
                                            <th class="data" width="10%">Chromosome</th>
                                            <th class="data" width="10%">&nbsp;</th>
                                        </tr>
                                        <m:iterate-collection collection="expressed_genes">
                                            <tr class="#?alt#">
                                                <td><a href="Controller?workflow=ViewExpressedGene&amp;gid=#:getGaid#" title="View Expressed Gene" class="data_link">#:getGenesymbol_ss#</a></td>
                                                <td>#:getName_ss#</td>
                                                <td>#:getChromoName#</td>
                                                <td>
                                                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                                        <% if (edithis == 13) {%>
                                                        <a href="Controller?workflow=EditExpressedGene&amp;gaid=#:getGaid#" class="navtext">edit</a>&nbsp;
                                                        <br/>
                                                        <a href="Controller?workflow=RemoveGene&amp;gaid=#:getGaid#" title="Delete Expressed Gene" onclick="return confirm('Delete Expressed Gene Completely?')" class="navtext">delete</a>
                                                        <br/>
                                                        <a href="Controller?workflow=UnAssignGeneModel&amp;gaid=#:getGaid#" onclick="return confirm('Unassign Expressed Gene?')" class="navtext">unassign</a>
                                                        <% }%>
                                                    </m:hide>
                                                </td>
                                            </tr>
                                        </m:iterate-collection>
                                    </table>
                                </div>
                                <!--subsection for Allele Information-->
                                <div class="subsection">
                                    <span>
			Allele Information
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a class="navtext" href="Controller?workflow=AssignStrainAllele">add</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
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
                                                <td>#:getSymbol_ss#</td>
                                                <td>#:getName_ss#</td>
                                                <td><a href="#:getMgi_url#" target="_blank">#:getMgi_id#</a></td>
                                                <td>#:getMutations#</td>
                                                <td>#:getAttributes#</td>
                                                <td>
                                                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                                        <% if (edithis == 13) {%>
                                                        <a href="Controller?workflow=EditStrainAllele&amp;strainalleleid=#:getId#&amp;transgc=<%=modeldto.getDistParam()%>&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">edit</a>
                                                        <br/>
                                                        <a href="Controller?workflow=RemoveStrainAllele&amp;strainalleleid=#:getId#&amp;transgc=<%=modeldto.getDistParam()%>&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" onclick="return confirm('Remove Allele?')" class="navtext">delete</a>
                                                        <% }%>
                                                    </m:hide>
                                                </td>
                                            </tr>
                                        </m:iterate-collection>
                                    </table>
                                </div>
                                <!--subsection for Recombination Efficiency-->
                                <div class="subsection">
                                    <span>
			Recombination Efficiency
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a href="Controller?workflow=EditRecombinationEfficiency&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">edit</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
                                    <table class="block_data">
                                        <tr class="alternatingOne">
                                            <td><jsp:getProperty name="modeldto" property="researchAppText"/></td>
                                        </tr>
                                    </table>
                                </div>
                                <!--subsection for Specificity-->
                                <div class="subsection">
                                    <span>
			Specificity
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a href="Controller?workflow=CreateExpressionModel&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
                                    <table class="block_data">
                                        <!--tr>
                                            <th class="data" width="10%">Anatomy</th>
                                            <th class="data" width="10%">Comment</th>
                                            <th class="data" width="70%">Picture</th>
                                            <th class="data" width="10%">&nbsp;</th>
                                        </tr-->
                                        <m:iterate-collection collection="expressions">
                                            <tr class="#?alt#">
                                                <!--td>#:getExanatomy#</td>
                                                <td>#:getExcomm#</td>
                                                <td>#:getExfiletable#
                                                    <hr/>
                                                #:getEmap_terms#
                                                <hr/>
                                                #:getMa_terms#
                                                </td-->
                                                <td width="90%">
                                                    <i style="margin-left: 20px; font-weight:bold">Anatomy:</i> #:getExanatomy#
                                                    <hr/>
                                                    <i style="margin-left: 20px; font-weight:bold">Comment:</i> #:getExcomm#
                                                    <hr/>
                                                    <i style="margin-left: 20px; font-weight:bold">Images:</i> #:getExfiletable#
                                                    <hr/>
                                                    <i style="margin-left: 20px; font-weight:bold">Site of Expression:</i> #:getMa_terms#
                                                    <hr/>
                                                    <i style="margin-left: 20px; font-weight:bold">Developmental Stage:</i> #:getEmap_terms#
                                                </td>
                                                <td>
                                                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                                        <% if (edithis == 13) {%>
                                                        <a href="Controller?workflow=EditExpressionModel&amp;exid=#:getExid#" class="navtext">edit</a>
                                                        <br/>
                                                        <a href="Controller?workflow=RemoveExpressionModel&amp;exid=#:getExid#" onclick="return confirm('Delete Specificity?')" class="navtext">delete</a>
                                                        <% }%>
                                                    </m:hide>
                                                </td>
                                            </tr>
                                        </m:iterate-collection>
                                    </table>
                                </div>
                                <!--subsection for Integration Site & Copy Number-->
                                <div class="subsection">
                                    <span>
			Integration Site &amp; Copy Number
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a href="Controller?workflow=CreateIntegrationCopy&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
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
                                                        <% if (edithis == 13) {%>
                                                        <a href="Controller?workflow=EditIntegrationCopy&amp;iscmid=#:getIscnid#" class="navtext">edit</a>
                                                        <br/>
                                                        <a href="Controller?workflow=RemoveIntegrationCopy&amp;iscmid=#:getIscnid#" onclick="return confirm('Delete Integration Site & Copy Number?')" class="navtext">delete</a>
                                                        <% }%>
                                                    </m:hide>
                                                </td>
                                            </tr>
                                        </m:iterate-collection>
                                    </table>
                                </div>
                            </div>
                            <div id="fragment-3">
                                <p></p>
                                <!--subsection for Handling & Genotyping Instructions-->
                                <div class="subsection">
                                    <span>
			Handling & Genotyping Instructions
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a href="Controller?workflow=CreateModelFileResource&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add file</a>
                                            <a href="Controller?workflow=CreateModelLinkResource&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add link</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
                                    <m:resource-simple-list resourceTreeCollection="resourceTree"/>
                                </div>
                                <!--subsection for References-->
                                <div class="subsection">
                                    <span>
			Primary Reference(s)
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a href="Controller?workflow=CreateModelFileReference&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add file</a>
                                            <a href="Controller?workflow=CreateModelLinkReference&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add link</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
                                    <table class="block_data">
                                        <tr>
                                            <th class="data" width="10%">PubMed</th>
                                            <th class="data" width="80%">Name</th>
                                            <!--th class="data" width="10%">Type</th-->
                                            <!--th class="data" width="30%">Comment</th-->
                                            <th class="data">&nbsp;</th>
                                        </tr>
                                        <m:iterate-collection collection="references_primary">
                                            <tr class="#?alt#">
                                                <td>#:getPubmed#</td>
                                                <td>
                                                    <a href="#:getResource#" target="#:getTarget#" title="View file/Visit link" class="data_link">#:getName#</a>
                                                    <br/><br/>
                                                    <i>#:getComm#</i>
                                                </td>
                                                <!--td>#:getType#</td-->
                                                <!--td>#:getComm#</td-->
                                                <td>
                                                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                                        <% if (edithis == 13) {%>
                                                        <a href="#:getEdit#&amp;refid=#:getRefid#" class="navtext">edit</a>
                                                        <br/>
                                                        <a href="Controller?workflow=RemoveModelReference&amp;refid=#:getRefid#" onclick="return confirm('Remove Reference?')" class="navtext">delete</a>
                                                        <% }%>
                                                    </m:hide>
                                                </td>
                                            </tr>
                                        </m:iterate-collection>
                                    </table>
                                </div>
                                <div class="subsection">
                                    <span>
			Additional References
                                        <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                            <% if (edithis == 13) {%>
                                            <a href="Controller?workflow=CreateModelFileReference&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add file</a>
                                            <a href="Controller?workflow=CreateModelLinkReference&amp;eid=<jsp:getProperty name="modeldto" property="eid"/>" class="navtext">add link</a>
                                            <% }%>
                                        </m:hide>
                                    </span>
                                    <table class="block_data">
                                        <tr>
                                            <th class="data" width="10%">PubMed</th>
                                            <th class="data" width="80%">Name</th>
                                            <!--th class="data" width="10%">Type</th-->
                                            <!--th class="data" width="30%">Comment</th-->
                                            <th class="data">&nbsp;</th>
                                        </tr>
                                        <m:iterate-collection collection="references">
                                            <tr class="#?alt#">
                                                <td>#:getPubmed#</td>
                                                <td>
                                                    <a href="#:getResource#" target="#:getTarget#" title="View file/Visit link" class="data_link">#:getName#</a>
                                                    <br/><br/>
                                                    <i>#:getComm#</i>
                                                </td>
                                                <!--td>#:getType#</td-->
                                                <!--td>#:getComm#</td-->
                                                <td>
                                                    <m:hide privilege="MODEL_W" suid="<%=modeldto.getSuid()%>">
                                                        <% if (edithis == 13) {%>
                                                        <a href="#:getEdit#&amp;refid=#:getRefid#" class="navtext">edit</a>
                                                        <br/>
                                                        <a href="Controller?workflow=RemoveModelReference&amp;refid=#:getRefid#" onclick="return confirm('Remove Reference?')" class="navtext">delete</a>
                                                        <% }%>
                                                    </m:hide>
                                                </td>
                                            </tr>
                                        </m:iterate-collection>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <!--some extra info-->
                        <p class="navtext">
                            <b>corresponding researcher: </b><a href="Controller?workflow=ViewUser&amp;id=<jsp:getProperty name="modeldto" property="contactId"/>" title="Contact Researcher"><jsp:getProperty name="modeldto" property="contactName"/></a>&nbsp;
                            <b>updated: </b><jsp:getProperty name="modeldto" property="ts"/>
                            <m:hide privilege="MODEL_ADM" suid="<%=modeldto.getSuid()%>">
                                &nbsp;<b>dissemination level: </b><jsp:getProperty name="modeldto" property="level"/>
                            </m:hide>
                        </p>
                    </form>
                </div>
            </div>
            <div id="clear-foot">&nbsp;</div>
        </div>
        <jsp:include page="/Foot"/>
    </body>
</html>