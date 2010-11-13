<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; add repository</title>
    <jsp:include page="/Header"/>
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() { 
                define('reponame', 'string', 'Repository Name', 1, 500);
            }        
    </script>
</head>
<body onload="init()">
<div id="wrap">
	<div id="panel-left">
		<jsp:include page="/PanelLeft"/>
	</div>
	<div id="panel-right">
		<jsp:include page="/PanelRightTop"/>        
        <div id="panel-right-rest">
        <form action="Controller" method="post">
            <span class="header_01">Add Repository</span>
        <table>
            <tr>
                <td><b>Repository Name</b></td>
            </tr>
            <tr>
                <td>
                    <input type="text" name="reponame" size="35"/>
                    <!--if it's not a str8 forward gen.back. value creation...-->
                    <% if(request.getParameter("eid")!=null){%>
                    <input type="hidden" name="eid" value='<%=request.getParameter("eid")%>'/>
                    <% } %>
                </td>
            </tr>
            <tr>
                <td><b>Has DB?</b></td>
            </tr>
            <tr>
                <td><select name="hasdb"><option value="0">no</option><option value="1">yes</option></select></td>
            </tr>
            <tr>
                <td><b>Mouse URL</b></td>
            </tr>
            <tr>
                <td><input type="text" name="mouseurl" size="35"/></td>
            </tr>
            <tr>
                <td><b>Repository URL</b></td>
            </tr>
            <tr>
                <td><input type="text" name="repourl" size="35"/></td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <input type="submit" name="save" value="Save" title="Save" onclick="validate();return returnVal;"/>
                        <input type="submit" name="back" value="Back" title="Back"/>
                    </p>
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
