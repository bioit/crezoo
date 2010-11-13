<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; add genetic background information</title>
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
            <span class="header_01">Add Genetic Background Information</span>
        <table>
            <tr>
                <td><b>DNA Origin</b>&nbsp;<a class="navtext" title="Add New Background Value" href="Controller?workflow=CreateGeneticBackgroundValueWhileAddingGeneticBackgroundInfo&amp;eid=<%=request.getAttribute("eid")%>&amp;bid=null">Add</a></td>
            </tr>
            <tr>
                <td>
                    <m:checkbox collection="genBacks" name="dna_origin" idGetter="getBid" textGetter="getBackname"/>
                </td>
            </tr>
            <tr>
                <td><b>Targeted Background</b>&nbsp;<a class="navtext" title="Add New Background Value" href="Controller?workflow=CreateGeneticBackgroundValueWhileAddingGeneticBackgroundInfo&amp;eid=<%=request.getAttribute("eid")%>&amp;bid=null">Add</a></td>
            </tr>
            <tr>
                <td>
                    <m:checkbox collection="genBacks" name="targeted_back" idGetter="getBid" textGetter="getBackname"/>
                </td>
            </tr>
            <tr>
                <td><b>Host Background</b>&nbsp;<a class="navtext" title="Add New Background Value" href="Controller?workflow=CreateGeneticBackgroundValueWhileAddingGeneticBackgroundInfo&amp;eid=<%=request.getAttribute("eid")%>&amp;bid=null">Add</a></td>
            </tr>
            <tr>
                <td>
                    <m:checkbox collection="genBacks" name="host_back" idGetter="getBid" textGetter="getBackname"/>
                </td>
            </tr>
            <tr>
                <td><b>Backcrossing Strain</b>&nbsp;<a class="navtext" title="Add New Background Value" href="Controller?workflow=CreateGeneticBackgroundValueWhileAddingGeneticBackgroundInfo&amp;eid=<%=request.getAttribute("eid")%>&amp;bid=null">Add</a></td>
            </tr>
            <tr>
                <td>
                    <m:checkbox collection="genBacks" name="backcrossing_strain" idGetter="getBid" textGetter="getBackname"/>
                </td>
            </tr>
            <tr>
                <td><b>Number of Backcrosses</b></td>
            </tr>
            <tr>
                <td>
                    <m:checkbox collection="backcrosseCollection" name="backcrosses" idGetter="toString" textGetter="toString"/>
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