<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="strain_type" scope="request" type="org.tgdb.model.modelmanager.StrainTypeDTO" />
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; edit strain type</title>
    <jsp:include page="/Header"/>
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() { 
                define('name', 'string', 'Name', 1, 500);
                define('abbreviation', 'string', 'Abbreviation', 1, 500);
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
            <input type="hidden" name="stid" value="<%=strain_type.getId() %>" />
            <span class="header_01">Edit Strain Type</span>
        <table>
            <tr>
                <td><b>Name</b></td>
            </tr>
            <tr>
                <td>
                    <input type="text" name="name" maxlength="500" value="<%=strain_type.getName() %>" />
                </td>
            </tr>
            <tr>
                <td><b>Abbreviation</b></td>
            </tr>
            <tr>
                <td>
                    <input type="text" name="abbreviation" maxlength="500" value="<%=strain_type.getAbbreviation() %>" />
                </td>
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