<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean type="org.tgdb.model.modelmanager.StrainAlleleDTO" id="strainallele" scope="request"/>
<% org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller"); %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; view allele</title>
    <jsp:include page="/Header"/>
    <link rel="stylesheet" type="text/css" href="css/crezoo-tabs.css" />
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
        <span class="header_01">View Allele</span>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <!--input type="submit" name="back" value="Back" title="Back"/--><a href="Controller?workflow=ViewStrainAlleles">Back</a>&nbsp;
                        <m:hide privilege="MODEL_W" pid="<%=caller.getPid()%>">
                                <a href="Controller?workflow=_EditStrainAllele&amp;strainalleleid=<%=strainallele.getId() %>">Edit</a>&nbsp;
                                <a href="Controller?workflow=DeleteStrainAllele&amp;strainalleleid=<%=strainallele.getId() %>" title="Delete" onClick="return confirm('Delete Allele? ')">Delete</a>
                        </m:hide>
                    </p>
                </td>
            </tr>
        </table>
        <table width="100%">
            <tr>
                <td><b>Allele Name:</b>&nbsp;<%=strainallele.getName_ss() %></td>
            </tr>
            <tr>
                <td><b>Allele Symbol:</b>&nbsp;<%=strainallele.getSymbol_ss() %></td>
            </tr>
            <tr>
                <td><b>Allele MGI ID:</b>&nbsp;<a href="<%=strainallele.getMgi_url() %>" target="_blank"><%=strainallele.getMgi_id() %></a></td>
            </tr>
            <!--tr>
                <td><b>Allele MGI URl:</b>&nbsp;<%=strainallele.getMgi_url() %></td>
            </tr-->
            <tr>
                <td><b>Mutation Made by</b>&nbsp;<%=strainallele.getMade_by() %></td>
            </tr>
            <tr>
                <td><b>Strain of Origin:</b>&nbsp;<%=strainallele.getOrigin_strain() %></td>
            </tr>
        </table>
        <br/>
        <table class="block3" width="100%">
            <tr>
                <th class="block">Related Mice</th>
            </tr>
            <m:iterate-collection collection="models">
            <tr class="#?alt#">
                <td><a href="Controller?workflow=ViewModel&amp;eid=#:getEid#" title="View Mouse">#:getLineName_ss#</a></td>
            </tr>
            </m:iterate-collection><tr>
                <th class="block">Promoters</th>
            </tr>
            <m:iterate-collection collection="promoters">
            <tr class="#?alt#">
                <td><a href="Controller?workflow=ViewPromoter&amp;gid=#:getGaid#" title="View Promoter" class="data_link">#:getGenesymbol_ss#</a></td>
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