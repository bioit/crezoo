<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; available genetic backgrounds index</title>
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
            <span class="header_01">Available Genetic Background Index</span>
            <table>
                <tr>
                    <td>&nbsp;</td>
                </tr>
            </table>
            <table class="data">
                <tr>                    
                    <th class="data" width="10%"><span>ID</span></th>
                    <th class="data" width="80%"><span>Background</span></th>
                    <th class="data" width="10%"><span>&nbsp;</span></th>
                </tr>
                <m:iterate-collection collection="avgenbacks">
                    <tr class="#?alt#">                        
                        <td>#:getAid#</td>
                        <td><a href="Controller?workflow=EditAvailableGeneticBackground&amp;aid=#:getAid#">#:getAvbackname#</a></td>
                        <td><a href="Controller?workflow=RemoveAvailableGeneticBackground&amp;aid=#:getAid#" title="Delete this background" onclick="return confirm('Remove background? PLEASE NOTE: ALL AVAILABILITY INFO RELEVANT TO THIS BACKGROUND WILL BE REMOVED FROM ALL MUTANTS!!!')">Delete</a></td>
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