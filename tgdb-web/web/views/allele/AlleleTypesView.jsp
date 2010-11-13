<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; allele types</title>
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
        <span class="header_01">Allele Types</span>
            <table class="data">
                <tr>
					<th class="data" width="20%"><span>Abbreviation</span></th>
                    <th class="data" width="60%"><span>Name</span></th>
					<th class="data"><span>&nbsp;</span></th>
                </tr>
                <m:iterate-collection collection="mutationtypes">
                    <tr class="#?alt#">
                        <td width="20%">#:getAbbreviation#</td>
						<td width="60%">#:getName#</td>
                        <td>
                            <a href="Controller?workflow=EditAlleleType&amp;mtid=#:getId#" class="navtext">edit</a>
                            <br/>
                            <a href="Controller?workflow=DeleteAlleleType&amp;mtid=#:getId#" class="navtext">delete</a>
                        </td>
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
