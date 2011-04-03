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
            </table><%--narrow selection menu--%>
            <table>
            <tr valign="bottom">
                <td>
                <b>promoter:</b>&nbsp;<m:checkbox collection="genes" onChange="this.form.submit()" name="gaid" idGetter="getGaid" textGetter="getGenesymbol_ss" selected="<%=fdm.getValue("gaid")%>" wildcardOption="true"/>
                &nbsp;<b>inducibility:</b>&nbsp;<m:checkbox collection="inducibilities" onChange="this.form.submit()" name="inducible" idGetter="toString" textGetter="toString" selected="<%=fdm.getValue("inducible")%>" wildcardOption="true"/>
                &nbsp;<b>expression:</b>&nbsp;<m:checkbox collection="mas" onChange="this.form.submit()" name="ma" idGetter="getOid" textGetter="getOid" selected="<%=fdm.getValue("ma")%>" wildcardOption="true"/>
                &nbsp;<b>developmental stage:</b>&nbsp;<m:checkbox collection="emaps" onChange="this.form.submit()" name="emap" idGetter="getOid" textGetter="getOid" selected="<%=fdm.getValue("emap")%>" wildcardOption="true"/>
                &nbsp;<b>researcher:</b>&nbsp;<m:checkbox collection="researchers" onChange="this.form.submit()" name="participantname" idGetter="toString" textGetter="toString" selected="<%=fdm.getValue("participantname")%>" wildcardOption="true"/>
                <!--&nbsp;and/or&nbsp;<b>strain:</b> &nbsp;<m:checkbox collection="strains" onChange="this.form.submit()" name="strain" idGetter="getStrainId" textGetter="getDesignation" selected="<%=fdm.getValue("strain")%>" wildcardOption="true"/>-->
                </td>
            </tr>
        </table>
            <table class="data">
                <tr>
                    <!--th class="data" width="10%"><input type="submit" title="order by ID" name="byID" value="ID" /></th-->
                    <th class="data" width="40%"><input type="submit" title="order by line name" name="byNAME" value="Line Name" /></th>
                    <th class="data" width="20%"><span>Promoters</span></th>
                    <th class="data" width="20%"><input type="submit" title="order by last inducibility" name="byINDUCIBILITY" value="Inducibility" /></th>
                    <th class="data" width="20%"><input type="submit" title="order by last update" name="byDATE" value="Last Update" /></th>
                </tr>
                <m:iterate-collection collection="modelsdto">
                    <tr class="#?alt#">
                        <!--td width="10%">#:getAccNr#</td-->
                        <td width="40%"><a href="Controller?workflow=ViewModel&amp;eid=#:getEid#" class="indexmain">#:getLineName_ss#</a></td>
                        <td width="20%">#:getPromoters_string#</td>
                        <td width="20%">#:getInducible#</td>
                        <td width="20%">#:getTs#</td>
                    </tr>
                </m:iterate-collection>
            </table>
            <table>
                <tr>
                    <td>
                      <m:navigation-buttons workflow="ViewModels" />
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