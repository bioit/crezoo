<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; create allele</title>
    <jsp:include page="/Header"/>
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() { 
                //define('symbol', 'string', 'Allele Symbol', 1, 500);
                //define('name', 'string', 'Allele Name', 1, 500);
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
            <span class="header_01">Assign Allele</span>
            <table>
                <%--<tr>
                    <td><b>Allele Symbol</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="symbol" maxlength="500" size="55"/></td>
                </tr>
                <tr>
                    <td><b>Allele Name</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="name" maxlength="500" size="55"/></td>
                </tr>--%>
                <tr>
                    <td><b>Select Allele</b> <a class="navtext" href="Controller?workflow=CreateStrainAllele">add</a></td>
                </tr>
                <tr>
                    <td><m:checkbox collection="alleles_unassigned" idGetter="getId" textGetter="getName" name="allele_id"/></td>
                </tr>
                <tr>
                    <td><b>Allele Type</b></td>
                </tr>
                <tr>
                    <td><m:checkbox collection="types" idGetter="getMutantid" textGetter="getName" name="type"/></td>
                </tr>
                <tr>
                    <td><b>Attributes</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="attribute" maxlength="500" size="55"/><br/><span style="font-size: 10px;font-style: italic">comma-separated values for multiple entries</span></td>
                </tr>
            </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <input type="submit" name="assign" value="Assign" title="Assign" onclick="validate();return returnVal;"/>
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