<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<% org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller"); %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; account</title>
    <jsp:include page="/Header"/>
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() {
                define('old', 'string', 'Old Password', 1, 500);
                define('p1', 'string', 'New Password', 1, 500);
                define('p2', 'string', 'Retyped New Password', 1, 500);
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
            <span class="header_01">Account for <%=caller.getName() %></span>
            <table>
                <tr>
                    <td>Old Password</td>
                    <td><input name="old" type="password" size="100"/></td>
                </tr>
                <tr>
                    <td>New Password</td>
                    <td><input name="p1" type="password" size="100"/></td>
                </tr>
                <tr>
                    <td>Retype New Password</td>
                    <td><input name="p2" type="password" size="100"/></td>
                </tr>
            </table>
            <table>
                <tr>
                    <td>
                        <p class="navtext">
                            <input type="submit" name="create" value="Save" title="Save" onclick="validate();return returnVal;"/>
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