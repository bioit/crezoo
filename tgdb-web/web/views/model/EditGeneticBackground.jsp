<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="modeldto" scope="request" type="org.tgdb.model.modelmanager.ExpModelDTO" />
<jsp:useBean id="genbackdto" scope="request" type="org.tgdb.model.modelmanager.GeneticBackgroundDTO" />
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; edit genetic background information</title>
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
            <span class="header_01">Edit Genetic Background Information</span>
        <input type="hidden" name="eid" value='<jsp:getProperty name="modeldto" property="eid"/>'/>
        <table>
            <tr>
                <td><b>DNA Origin</b></td>
            </tr>
            <tr>
                <td>
                    <m:checkbox collection="genBacks" selected='<%=genbackdto.getDna_origin()%>' name="dna_origin" idGetter="getBid" textGetter="getBackname"/>
                </td>
            </tr>
            <tr>
                <td><b>Targeted Background</b></td>
            </tr>
            <tr>
                <td>
                    <m:checkbox collection="genBacks" selected='<%=genbackdto.getTargeted_back()%>' name="targeted_back" idGetter="getBid" textGetter="getBackname"/>
                </td>
            </tr>
            <tr>
                <td><b>Host Background</b></td>
            </tr>
            <tr>
                <td>
                    <m:checkbox collection="genBacks" selected='<%=genbackdto.getHost_back()%>' name="host_back" idGetter="getBid" textGetter="getBackname"/>
                </td>
            </tr>
            <tr>
                <td><b>Backcrossing Strain</b></td>
            </tr>
            <tr>
                <td>
                    <m:checkbox collection="genBacks" selected='<%=genbackdto.getBackcrossing_strain()%>' name="backcrossing_strain" idGetter="getBid" textGetter="getBackname"/>
                </td>
            </tr>
            <tr>
                <td><b>Number of Backcrosses</b></td>
            </tr>
            <tr>
                <td>
                    <m:checkbox collection="backcrosseCollection" selected='<%=genbackdto.getBackcrosses()%>' name="backcrosses" idGetter="toString" textGetter="toString"/>
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