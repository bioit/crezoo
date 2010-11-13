<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean type="org.tgdb.model.modelmanager.StrainAlleleDTO" id="strainallele" scope="request"/>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; edit allele</title>
    <jsp:include page="/Header"/>
    <link rel="stylesheet" type="text/css" href="css/crezoo-tabs.css" />
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() { 
                define('name', 'string', 'Allele Name', 1, 500);
                define('symbol', 'string', 'Allele Symbol', 1, 500);
                define('mgiid', 'num', 'Allele MGI ID', null, 500);
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
            <span class="header_01">Edit Allele</span>
        <table width="100%">
            <tr>
                <td><b>Allele Name</b></td>
            </tr>
            <tr>
                <td><input type="text" name="name" value="<jsp:getProperty name="strainallele" property="name"/>" size="55" maxlength="500" /></td>
            </tr>
            <tr>
                <td><b>Allele Symbol</b></td>
            </tr>
            <tr>
                <td><input type="text" name="symbol" value="<jsp:getProperty name="strainallele" property="symbol"/>" size="55" maxlength="500" /></td>
            </tr>
            <tr>
                <td><b>Allele MGI ID</b></td>
            </tr>
            <tr>
                <td><input type="text" name="mgi_id" value="<jsp:getProperty name="strainallele" property="mgi_id"/>" size="55" maxlength="500" /></td>
            </tr>
            <tr>
                <td><b>Allele MGI URl</b></td>
            </tr>
            <tr>
                <td><input type="text" name="mgi_url" value="<jsp:getProperty name="strainallele" property="mgi_url"/>" size="55" maxlength="500" /></td>
            </tr>
            <tr>
                <td><b>Mutation Made by</b></td>
            </tr>
            <tr>
                <td><input type="text" name="made_by" value="<jsp:getProperty name="strainallele" property="made_by"/>" size="55" maxlength="500" /></td>
            </tr>
            <tr>
                <td><b>Strain of Origin</b></td>
            </tr>
            <tr>
                <td><input type="text" name="origin_strain" value="<jsp:getProperty name="strainallele" property="origin_strain"/>" size="55" maxlength="500" /></td>
            </tr>
        </table>
        <br/>
        <table class="block3" width="100%">
            <tr>
                <th class="block">Promoters&nbsp;<a class="navtext" href="Controller?workflow=AddGeneToAllele" title="Add Promoter">Add</a></th>
            </tr>
            <m:iterate-collection collection="promoters">
            <tr class="#?alt#">
                <td><a class="navtext" href="Controller?workflow=RemoveGeneFromAllele&amp;gid=#:getGaid#" title="Remove Promoter" onclick="return confirm('Remove Promoter?')">Remove</a>&nbsp;#:getName_ss#</td>
            </tr>
            </m:iterate-collection>
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