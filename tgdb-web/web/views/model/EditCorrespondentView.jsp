<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="correspondent" scope="request" type="org.tgdb.project.projectmanager.UserDTO" />
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; edit correspondent</title>
    <jsp:include page="/Header"/>
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() {
                define('name', 'string', 'Name', 1, 500);
                define('email', 'string', 'Email', 1, 500);
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
            <span class="header_01">Edit Correspondent</span>
            <input type="hidden" name="cid" value="<%=correspondent.getId() %>" />
            <table>
                <tr>
                    <td>Name</td>
                </tr>
                <tr>
                    <td><input type="text" name="name" size="55" value="<%=correspondent.getName() %>"/></td>
                </tr>
                <tr>
                    <td>Email</td>
                </tr>
                <tr>
                    <td><input type="text" name="email" size="55" value="<%=correspondent.getEmail() %>"/></td>
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