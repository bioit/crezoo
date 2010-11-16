<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="gene" scope="request" type="org.tgdb.model.modelmanager.GeneDTO" />
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; edit promoter</title>
    <jsp:include page="/Header"/>
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() {
                define('name', 'string', 'Promoter Name', 1, 500);
                define('genesymbol', 'string', 'Promoter Symbol', 1, 500);
                define('mgiid', 'num', 'MGI ID', null, 500);
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
            <span class="header_01">Edit Promoter</span>
            <input type="hidden" name="gaid" value="<jsp:getProperty name='gene' property='gaid'/>"/>
            <input type="hidden" name="mgiid" size="35" value='<jsp:getProperty name="gene" property="mgiid"/>'/>
            <table>
                <tr>
                    <td><b>Promoter Name</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="name" size="35" value='<jsp:getProperty name="gene" property="name"/>'/></td>
                </tr>
                <tr>
                    <td><b>Promoter Symbol</b></td>
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
                <!--tr>
                    <td><b>MGI ID</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="mgiid" size="35" value='<jsp:getProperty name="gene" property="mgiid"/>'/></td>
                </tr-->
                <tr>
                    <td><b>Driver Note</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="driver_note" size="35" value='<jsp:getProperty name="gene" property="driver_note"/>'/></td>
                </tr>
                <tr>
                    <td><b>Common Name</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="common_name" size="35" value='<jsp:getProperty name="gene" property="common_name"/>'/></td>
                </tr>
            </table>
                <table>
            <tr><td colspan="3"><b>External IDs</b> <a href="Controller?workflow=AddPromoterLink" class="navtext">add</a></td></tr>
            <m:iterate-collection collection="promoter_links">
                <tr class="#?alt#">
                    <td>#:getRepository#</td>
                    <td><a href="#:getStrainurl#" title="#:getExternalid#" target="_blank">#:getExternalid#</a></td>
                    <td><a href="Controller?workflow=DeletePromoterLink&amp;plid=#:getId#" class="navtext">delete</a></td>
                </tr>
            </m:iterate-collection>
        </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <input type="submit" name="save_promoter" value="Save" title="Save" onclick="validate();return returnVal;"/>
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
