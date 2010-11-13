<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; edit strain</title>
    <jsp:include page="/Header"/>
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() { 
                define('designation', 'string', 'Designation', 1, 500);
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
            <span class="header_01">Edit Strain</span>
        <table>
            <tr>
                <td><b>Designation</b></td>
            </tr>
            <tr>
                <td>
                    <input type="text" name="designation" value="<jsp:getProperty name="straindto" property="designation"/>" maxlength="500" />
                </td>
            </tr>
        </table>
        <table>
            <tr><td colspan="3"><b>External IDs</b> <a href="Controller?workflow=AddStrainLink" class="navtext">add</a></td></tr>
            <m:iterate-collection collection="strainlinks">
                <tr class="#?alt#">
                    <td>#:getRepository#</td>
                    <td><a href="#:getStrainurl#" title="#:getExternalid#" target="_blank">#:getExternalid#</a></td>
                    <td><a href="Controller?workflow=DeleteStrainLink&amp;slid=#:getId#" class="navtext">delete</a></td>
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