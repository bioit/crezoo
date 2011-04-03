<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
    org.tgdb.form.FormDataManager fdm = (org.tgdb.form.FormDataManager)request.getAttribute("fdm_alleles");
    //org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller");
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; allele index</title>
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
            <span class="header_01">Allele Index</span>
            <table>
                <tr>
                    <td>
                      <m:navigation-buttons workflow="ViewStrainAlleles" showText='true'/>
                    </td>
                </tr>
            </table><table>
            <tr valign="bottom">
                <td>
                <b>promoter:</b>&nbsp;<m:checkbox collection="promoters" onChange="this.form.submit()" name="promoter" idGetter="getGaid" textGetter="getGenesymbol_ss" selected="<%=fdm.getValue("promoter")%>" wildcardOption="true"/>
                &nbsp;<b>made by:</b>&nbsp;<m:checkbox collection="madebys" onChange="this.form.submit()" name="made_by" idGetter="toString" textGetter="toString" selected="<%=fdm.getValue("made_by")%>" wildcardOption="true"/>
                &nbsp;<b>inducibility:</b>&nbsp;<m:checkbox collection="inducibilities" onChange="this.form.submit()" name="inducible" idGetter="toString" textGetter="toString" selected="<%=fdm.getValue("inducible")%>" wildcardOption="true"/>
                </td>
            </tr>
        	</table>
            <table class="data">
                <tr>
                    <th class="data" width="10%"><input type="submit" title="order by id" name="byID" value="ID" /></th>
                    <th class="data" width="40%"><input type="submit" title="order by symbol" name="bySYMBOL" value="Symbol" /></th>
                    <th class="data" width="40%"><input type="submit" title="order by name" name="byNAME" value="Name" /></th>
                    <th class="data" width="10%"><span>MGI ID</span></th>
                </tr>
                <m:iterate-collection collection="strain_alleles">
                    <tr class="#?alt#">
                        <td width="10%">#:getId#</td>
                        <td width="40%"><a href="Controller?workflow=ViewStrainAllele&amp;strainalleleid=#:getId#">#:getSymbol_ss#</a></td>
                        <td width="40%">#:getName_ss#</td>
                        <td width="10%"><a href="#:getMgi_url#" target="_blank">#:getMgi_id#</a></td>
                    </tr>
                </m:iterate-collection>
            </table><table>
                <tr>
                    <td>
                      <m:navigation-buttons workflow="ViewStrainAlleles"/>
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