<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; create trangenic mouse</title>
    <jsp:include page="/Header"/>
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
        function init() { 
            define('alias', 'string', 'Line Name', 1, 500);
            define('researchAppsText', 'string', 'Recombination Efficiency', 1, 500);
            define('comm', 'string', 'Comment', 1, 500);
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
            <span class="header_01">Create Transgenic Mouse</span>
        <input type="hidden" name="geneticBackground" value="n.a." />
        <input type="hidden" name="availability" value="n.a." />
        <input type="hidden" name="raid" value="9001"/>
        
        <table>
            <tr>
                <td><b>Line name</b></td>                            
            </tr>
            <tr>
                <td><input type="text" name="alias" size="55" maxlength="500"/></td>
            </tr>
            <tr>
                <td>
                    <m:hide-collection collection="samplingunits" limit="1">
                    <b>Sampling unit</b><br/>
                    </m:hide-collection>
                    <m:su-combobox hideEmpty="yes"/>
                </td>
            </tr>
            <tr>
                <td><b>Recombination Efficiency</b></td>
            </tr>
            <tr>
                <td><textarea cols="55" rows="4" name="researchAppsText"></textarea></td>
            </tr>
            <tr>
                <td><b>Corresponding Researcher</b> <a href="Controller?workflow=CreateCorrespondent" class="navtext">add new</a></td>
            </tr>   
            <tr>
                <td>
                    <m:checkbox collection="users" idGetter="getId" textGetter="getName" name="contactId"/>
                </td>
            </tr>
            <tr>
                <td><b>Authors comments</b></td>
            </tr>
            <tr>
                <td><textarea cols="55" rows="4" name="comm"></textarea></td>
            </tr>
            <tr>
                <td><b>Desired Dissemination Level</b></td>
            </tr>
            <tr>
                <td><m:checkbox collection="desired_levels" idGetter="toString" textGetter="toString" name="desired_level"/></td>
            </tr>
            <tr>
                <td><b>Donating investigator</b></td>
            </tr>
            <tr>
                <td><input type="text" name="donating_investigator" size="55" maxlength="500"/></td>
            </tr>
            <tr>
                <td><b>Inducible</b></td>
            </tr>
            <tr>
                <td><m:checkbox collection="inducibles" idGetter="toString" textGetter="toString" name="inducible"/></td>
            </tr>
            <tr>
                <td><b>Former names</b></td>
            </tr>
            <tr>
                <td><input type="text" name="former_names" size="55" maxlength="500"/></td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <input type="submit" name="create" value="Save" title="Save Tg Mouse" onclick="validate();return returnVal;"/>
                        <input type="submit" name="back" value="Back" title="Go Back"/>
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
