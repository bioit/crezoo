<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; repositories index</title>
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
            <span class="header_01">Repositories Index</span>
            <table>
                <tr>
                    <td>&nbsp;</td>
                </tr>
            </table>
            <table class="data">
                <tr>                    
                    <th class="data" width="10%"><span>ID</span></th>
                    <th class="data" width="80%"><span>Repository</span></th>
                    <th class="data" width="10%"><span>&nbsp;</span></th>
                </tr>
                <m:iterate-collection collection="repositories">
                    <tr class="#?alt#">                        
                        <td>#:getRid#</td>
                        <td><a href="Controller?workflow=EditRepository&amp;rid=#:getRid#">#:getReponame#</a></td>
                        <td><a class="navtext" href="Controller?workflow=RemoveRepository&amp;rid=#:getRid#" title="Delete this repository" onclick="return confirm('Remove repository? PLEASE NOTE: ALL AVAILABILITY INFO RELEVANT TO THIS REPOSITORY WILL BE REMOVED FROM ALL MUTANTS!!!')">Delete</a></td>
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