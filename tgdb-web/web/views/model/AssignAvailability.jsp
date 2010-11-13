<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; add availability</title>
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
            <span class="header_01">Add Availability Information</span>
            <table>
                <tr>
                    <td><b>Repository</b>&nbsp;<a class="navtext" href="Controller?workflow=AddRepositoryWhileAddingAvgenBackInfo&amp;eid=<%=request.getAttribute("eid")%>" title="Add A New Repository">Add</a></td>
                </tr>
                <tr>
                    <td>
                        <m:checkbox collection="repositories" name="repositories" idGetter="getRid" textGetter="getReponame"/>
                    </td>
                </tr>
                <tr>
                    <td><b>Genetic Background</b>&nbsp;<a class="navtext" href="Controller?workflow=AddAvailableGeneticBackgroundWhileAddingAvgenBackInfo&amp;eid=<%=request.getParameter("eid")%>" title="Add A New Background">Add</a></td>
                </tr>
                <tr>
                    <td>
                        <m:checkbox collection="avgenbacks" name="avgenbacks" idGetter="getAid" textGetter="getAvbackname"/>
                    </td>
                </tr>
                <tr>
                    <td><b>Strain State</b></td>
                </tr>
                <tr>
                    <td>
                        <m:checkbox collection="states" name="state" idGetter="getId" textGetter="getName" selected="1006"/>
                    </td>
                </tr>
                <tr>
                    <td><b>Strain Type</b></td>
                </tr>
                <tr>
                    <td>
                        <m:checkbox collection="types" name="type" idGetter="getId" textGetter="getName" selected="1016"/>
                    </td>
                </tr>
            </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <input type="submit" name="create" value="Save" title="Save" onclick="validate();return returnVal;"/>
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
 
