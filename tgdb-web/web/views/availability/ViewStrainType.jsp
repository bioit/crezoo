<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="strain_type" scope="request" type="org.tgdb.model.modelmanager.StrainTypeDTO" />
<% org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller"); %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; view strain type</title>
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
            <span class="header_01">View Strain Type</span>
            <p class="navtext">
                        <m:hide privilege="MODEL_W" pid="<%=caller.getPid()%>">
                                <a href="Controller?workflow=EditStrainType&amp;stid=<%=strain_type.getId() %>">Edit</a>&nbsp;
                                <a href="Controller?workflow=DeleteStrainType&amp;stid=<%=strain_type.getId() %>" title="Delete" onClick="return confirm('Remove Strain Type? ')">Delete</a>
                        </m:hide>
                        <input type="submit" name="back" value="Back" title="Back"/>
            </p>
        <table>
            <tr>
                <td><b>Name:</b>&nbsp;<%=strain_type.getName() %></td>
            </tr>
            <tr>
                <td><b>Abbreviation:</b>&nbsp;<%=strain_type.getAbbreviation() %></td>
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