<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; login</title>
	<link rel="stylesheet" type="text/css" href="css/crezoo-tabs.css" />
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
            <span class="header_01">Login</span>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <input type="submit" name="back" value="back" title="Back" />
                        <input type="submit" name="login" value="login" title="Login" />
                    </p>
                </td>
            </tr>
        </table>
    <table>
        <tr>
            <td>
                <b>username:</b>&nbsp;<input name="usr" maxlength="19" size="19" type="text" />
            </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr>
            <td>
                <b>password:</b>&nbsp;<input name="pwd" maxlength="19" size="19" class="logit" type="password" />
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
