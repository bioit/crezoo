<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="straindto" scope="request" type="org.tgdb.model.modelmanager.StrainDTO" />
<% org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller"); %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; strain</title>
	<link rel="stylesheet" type="text/css" href="css/crezoo-tabs.css" />
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
            <span class="header_01"><%=straindto.getDesignation_ss() %></span>
            <table>
                <tr>
                    <td>
                        <p class="navtext">
                            <m:hide privilege="MODEL_W" pid="<%=caller.getPid() %>">
                                <a href="Controller?workflow=_EditStrain&amp;strainid=<%=straindto.getStrainId() %>">Edit</a>
                                <a href="Controller?workflow=DeleteStrain&amp;strainid=<%=straindto.getStrainId() %>" title="Delete this strain" onClick="return confirm('Remove strain? ')">Delete</a>
                            </m:hide>
                            <input type="submit" name="back" value="back" title="Back" />
                        </p>
                    </td>
                </tr>
            </table>
            <table>
                <tr class="block">
                    <td class="block">
                        <b>Designation:</b>&nbsp;<%=straindto.getDesignation_ss() %>
                    </td>
				</tr>
				<tr class="block">
                    <td class="block">
                        <b>Type:</b>&nbsp;<%=straindto.getStrainTypeNames() %>
                    </td>
                </tr>
                <tr class="block">
                    <td class="block">
                        <b>State:</b>&nbsp;<%=straindto.getStrainStateNames() %>
                    </td>
                </tr>
            </table>
			<table class="block3">
                <tr><th colspan="2" class="block">External IDs</th></tr>
                <m:iterate-collection collection="strainlinks">
                    <tr class="#?alt#">
                        <td>#:getRepository#</td>
                        <td><a href="#:getStrainurl#" title="#:getExternalid#" target="_blank">#:getExternalid#</a></td>
                    </tr>
                </m:iterate-collection>
            </table>
    </form>
    </div>
</div>
<div id="clear-foot">&nbsp;</div>
</div>
<jsp:include page="/Foot"/>
</body>
</html>