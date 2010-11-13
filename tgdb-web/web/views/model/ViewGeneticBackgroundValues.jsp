<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; genetic backgrounds index</title>
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
            <span class="header_01">Genetic Background Index</span>
            <table>
                <tr>
                    <td>&nbsp;</td>
                </tr>
            </table>
            <table class="data">
                <tr>
                    <th class="data" width="10%"><span>ID</span></th>
                    <th class="data" width="90%"><span>Background</span></th>
                </tr>
                <m:iterate-collection collection="genBacks">
                    <tr class="#?alt#">                        
                        <td>#:getBid#</td>
                        <td><a href="Controller?workflow=EditGeneticBackgroundValue&amp;bid=#:getBid#">#:getBackname#</a></td>
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