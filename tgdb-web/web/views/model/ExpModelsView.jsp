<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
    org.tgdb.form.FormDataManager fdm = (org.tgdb.form.FormDataManager)request.getAttribute("formdata");
    org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller");
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; mice index</title>
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
            <span class="header_01">Mice Index</span>
            <table>
                <tr>
                    <td>
                      <m:navigation-buttons workflow="ViewModels" showText='true'/>
                    </td>
                </tr>
            </table>
            <table class="data">
                <tr>
                    <th class="data" width="10%"><input type="submit" title="order by ID" name="byID" value="ID" /></th>
                    <th class="data" width="70%"><input type="submit" title="order by line name" name="byNAME" value="Line Name" /></th>
                    <th class="data"><input type="submit" title="order by last update" name="byDATE" value="Last Update" /></th>
                </tr>
                <m:iterate-collection collection="modelsdto">
                    <tr class="#?alt#">
                        <td width="10%">#:getAccNr#</td>
                        <td width="70%"><a href="Controller?workflow=ViewModel&amp;eid=#:getEid#" class="indexmain">#:getLineName_ss#</a></td>
                        <td>#:getTs#</td>
                    </tr>
                </m:iterate-collection>
            </table>
            <table>
                <tr>
                    <td>
                      <m:navigation-buttons workflow="ViewModels"/>
                    </td>
                </tr>
            </table>
            <%--narrow selection menu--%>
            <table>
            <tr valign="bottom">
                <td>
				narrow index by <b>transgene:</b> &nbsp;<m:checkbox collection="genes" onChange="this.form.submit()" name="gaid" idGetter="getGaid" textGetter="getName" selected="<%=fdm.getValue("gaid")%>" wildcardOption="true"/>
                &nbsp;and/or&nbsp;<b>researcher:</b> &nbsp;<m:checkbox collection="researchers" onChange="this.form.submit()" name="participantname" idGetter="toString" textGetter="toString" selected="<%=fdm.getValue("participantname")%>" wildcardOption="true"/></td>
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