<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="gene" scope="request" type="org.tgdb.model.modelmanager.GeneDTO" />
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; trangene</title>
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
            <span class="header_01"><jsp:getProperty name="gene" property="name_ss"/>&nbsp;&mdash;&nbsp;<jsp:getProperty name="gene" property="genesymbol_ss"/></span>
            <table>
                <tr>
                    <td>
                        <p class="navtext">
                            <m:hide privilege="MODEL_W" pid="<%=gene.getPid()%>">
                                <a href="Controller?workflow=EditGene&amp;gaid=<jsp:getProperty name="gene" property="gaid"/>">Edit</a>&nbsp;
                                <a href="Controller?workflow=RemoveGene&amp;gaid=<jsp:getProperty name="gene" property="gaid"/>" title="Delete this transgene">Delete</a>
                            </m:hide>
                            <input type="submit" name="back" value="back" title="Back" />
                        </p>
                    </td>
                </tr>
            </table>
        	<input type="hidden" name="gaid" value='<jsp:getProperty name="gene" property="gaid"/>'/>
        <table>
            <tr class="block">
                <td class="block">
                    <b>Name:</b>&nbsp;<jsp:getProperty name="gene" property="name_ss"/>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="block">
                <td class="block">
                    <b>Symbol:</b>&nbsp;<jsp:getProperty name="gene" property="genesymbol_ss"/>
                </td>            
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="block">
                <td class="block">
                    <b>Chromosome:</b>&nbsp;<jsp:getProperty name="gene" property="chromoName"/>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="block">
                <td class="block">
                    <b>MGI ID:</b>&nbsp;<jsp:getProperty name="gene" property="mgiurl"/>
                    <%--<a href="http://www.informatics.jax.org/searches/accession_report.cgi?id=MGI:<jsp:getProperty name="gene" property="mgiid"/>" target="_blank">MGI:<jsp:getProperty name="gene" property="mgiid"/></a>--%>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="block">
                <td class="block">
                    <b>ENTREZ ID:</b>&nbsp;<jsp:getProperty name="gene" property="entrezurl"/>
                </td>            
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="block">
                <td class="block">
                    <b>ENSEMBL ID:</b>&nbsp;<jsp:getProperty name="gene" property="ensemblurl"/>
                </td>            
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="block">
                <td class="block"><b>Expression:</b>&nbsp;<jsp:getProperty name="gene" property="geneexpress"/></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="block">
                <td class="block"><b>Molecular Note:</b>&nbsp;<jsp:getProperty name="gene" property="molecular_note"/></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="block">
                <td class="block"><b>Molecular Note URl:</b>&nbsp;<jsp:getProperty name="gene" property="molecular_note_link"/></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="block">
                <td class="block">
                    <b>Comment:</b>&nbsp;<jsp:getProperty name="gene" property="comm"/>
                </td>                
            </tr>      
        </table>
		<br/>
        <table class="block3" width="100%">
            <tr><th class="block">Related Mice</th></tr>
            <tr>
                <td>
                    <table  class="block_data">
                        <tr>
                            <th class="data" width="100%">&nbsp;</th>
                        </tr>
                        <m:iterate-collection collection="models">
                            <tr class="#?alt#">
                                <td>
                                    <a href="Controller?workflow=ViewModel&amp;eid=#:getEid#" title="View Related Mouse" class="data_link">#:getLineName_ss#</a>
                                    <br/><br/>
                                    <i>#:getComm#</i>
                                    <br/><br/>
				</td>
                            </tr>
                        </m:iterate-collection>         
                    </table>  
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <b>User:</b>&nbsp;<a href="Controller?workflow=ViewUser&amp;id=<jsp:getProperty name="gene" property="userId"/>" title="View the details for this user"><jsp:getProperty name="gene" property="userName"/></a> 
                        &nbsp;
                        <b>Updated:</b>&nbsp;<jsp:getProperty name="gene" property="ts"/>
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
