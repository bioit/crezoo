<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; correspondents</title>
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
            <span class="header_01">Correspondents</span>
            <table class="data">
				<tr>
					<th class="data" width="50%"><span>Name</span></th>
					<th class="data" width="40%"><span>Email</span></th>
					<th class="data"><span>&nbsp;</span></th>
				</tr>
                <m:iterate-collection collection="correspondents">
                    <tr class="#?alt#">
                        <td width="50%">#:getName#</td>
                        <td width="40%">#:getEmail#</td>
                        <td><a href="Controller?workflow=EditCorrespondent&amp;cid=#:getId#" class="navtext">edit</a></td>
                    </tr>
                </m:iterate-collection>
            </table>
            <table>
        </table>
       </form>
        </div>
</div>
	<div id="clear-foot">&nbsp;</div>
</div>
<jsp:include page="/Foot"/>
</body>
</html>