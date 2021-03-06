<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; create promoter link</title>
    <jsp:include page="/Header"/>
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() { 
                define('repository', 'string', 'Repository', 1, 500);
                define('externalid', 'string', 'External ID', 1, 500);
                define('strainurl', 'string', 'URl', 1, 500);
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
            <span class="header_01">Create Promoter Link</span>
        <table>
            <tr>
                <td><b>Repository</b></td>
            </tr>
            <tr>
                <td>
                    <input type="text" name="repository" maxlength="500" />
                </td>
            </tr>
            <tr>
                <td><b>External ID</b></td>
            </tr>
            <tr>
                <td>
                    <input type="text" name="externalid" maxlength="500" />
                </td>
            </tr>
            <tr>
                <td><b>URl</b></td>
            </tr>
            <tr>
                <td>
                    <input type="text" name="strainurl" maxlength="1000" />
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <input type="submit" name="create" value="Create" title="Create" onclick="validate();return returnVal;"/>
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