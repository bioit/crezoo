<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; create promoter</title>
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
            <span class="header_01">Create Promoter</span>
            <table>
                <tr>
                    <td><b>Promoter Name</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="name" size="35"/></td>
                </tr>
                <tr>
                    <td><b>Promoter Symbol</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="genesymbol" size="35"/></td>
                </tr>
                <tr>
                    <td><b>Chromosome</b></td>
                </tr>
                <tr>
                    <td><m:checkbox collection="chromosomes" name="chromosome" idGetter="getCid" textGetter="getName" selected="24"/></td>
                </tr>
                <tr>
                    <td><b>MGI ID</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="mgiid" size="35" value="0"/></td>
                </tr>
                <tr>
                    <td><b>Driver Note</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="driver_note" size="35"/></td>
                </tr>
                <tr>
                    <td><b>Common Name</b></td>
                </tr>
                <tr>
                    <td><input type="text" name="common_name" size="35"/></td>
                </tr>
            </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <input type="submit" name="create_promoter" value="Save" title="Save" onclick="validate();return returnVal;"/>
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
