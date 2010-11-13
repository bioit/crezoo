<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="gene" scope="request" type="org.tgdb.model.modelmanager.GeneDTO" />
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; edit transgene</title>
    <jsp:include page="/Header"/>
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() { 
                define('name', 'string', 'Transgene Name', 1, 500);
                define('genesymbol', 'string', 'Transgene Symbol', 1, 500);
                define('mgiid', 'num', 'MGI ID', null, 500);
                define('idgene', 'num', 'ENTREZ ID', null, 500);
                define('idensembl', 'string', 'ENSEMBL ID', 1, 500);
            }        
    </script>
</head>
<body onload="init()">
<div id="wrap">
	<div id="panel-left">
		<jsp:include page="/PanelLeft"/>
	</div>
	<div id="panel-right">
		<jsp:include page="/PanelRightTop"/>        
        <div id="panel-right-rest">
        <form action="Controller" method="post">
            <span class="header_01">Edit Transgene</span>
            <input type="hidden" name="gaid" value="<jsp:getProperty name='gene' property='gaid'/>"/>
            <table>
                <tr>
                    <td><b>Transgene Name</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="name" size="35" value='<jsp:getProperty name="gene" property="name"/>'/></td>
                </tr>
                <tr>
                    <td><b>Transgene Symbol</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="genesymbol" size="35" value='<jsp:getProperty name="gene" property="genesymbol"/>'/></td>
                </tr>
                <tr>
                    <td><b>Chromosome</b></td>
                </tr>
                <tr>
                    <td><m:checkbox collection="chromosomes" name="chromosome" idGetter="getCid" textGetter="getName" selected='<%=gene.getCid()%>'/></td>
                </tr>
                <tr>
                    <td><b>MGI ID</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="mgiid" size="35" value='<jsp:getProperty name="gene" property="mgiid"/>'/></td>
                </tr>
                <tr>
                    <td><b>ENTREZ ID</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="idgene" size="35" value='<jsp:getProperty name="gene" property="idgene"/>'/></td>
                </tr>
                <tr>
                    <td><b>ENSEMBL ID</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="idensembl" size="35" value='<jsp:getProperty name="gene" property="idensembl"/>'/></td>
                </tr>
                <tr>
                    <td><b>Expression</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="geneexpress" size="35" value='<jsp:getProperty name="gene" property="geneexpress"/>'/></td>
                </tr>
                <tr>
                    <td><b>Molecular Note</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="molecular_note" size="35" value='<jsp:getProperty name="gene" property="molecular_note"/>'/></td>
                </tr>
                <tr>
                    <td><b>Molecular Note URl</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="molecular_note_url" size="35" value='<jsp:getProperty name="gene" property="molecular_note_link"/>'/></td>
                </tr>
                <tr>
                    <td><b>Comment</b></td>
                </tr>
                <tr>
                    <td><textarea cols="35" rows="6" name="comm"><jsp:getProperty name="gene" property="comm"/></textarea></td>
                </tr>
            </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <input type="submit" name="save" value="Save" title="Save" onclick="validate();return returnVal;"/>
                        <input type="submit" name="back" value="Back" title="Back"/>
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
