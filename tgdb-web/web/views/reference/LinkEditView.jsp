<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="linkdto" scope="request" class="org.tgdb.resource.resourcemanager.LinkDTO" />
<jsp:useBean id="refdto" scope="request" class="org.tgdb.model.modelmanager.ReferenceDTO" />
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; link edit</title>
    <jsp:include page="/Header"/>
    <link rel="stylesheet" type="text/css" href="css/crezoo-tabs.css" />
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() { 
                define('name', 'string', 'Name', null, 500);
                define('url', 'string', 'URL', null, 500);
                define('comm', 'string', 'Comment', null, 500);
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
    <form method="post" action="Controller">
        <span class="header_01">Reference Link Edit</span>
        <table>
            <input type="hidden" name="refid" value="<%=refdto.getRefid() %>" />
            <tr>
                <td><b>Name</b></td>
            </tr>
            <tr>
                <td><input type="text" name="name" size="55" maxlength="500" value='<%=refdto.getName() %>'/></td>
            </tr>
            <tr>
                <td><b>URL</b></td>
            </tr>
            <tr>
                <td><input type="text" name="url" size="55" maxlength="500" value='<jsp:getProperty name="linkdto" property="url"/>'/></td>
            </tr>
            <tr>
                <td><b>Pubmed ID</b></td>
            </tr>
            <tr>
                <td><input type="text" name="pubmed" size="55" maxlength="500" value='<%=refdto.getPubmed() %>'/></td>
            </tr>
            <tr>
                <td>
                    <b>Is this the Primary Publication?</b>
                    <input type="radio" name="primary" <% if(refdto.getPrimary()){ %>checked="checked"<%}%> value="true"/> Yes <input type="radio" name="primary" <% if(!refdto.getPrimary()){ %>checked="checked"<%}%> value="false" /> No
                </td>
            </tr>
            <tr>
                <td><b>Comment</b></td>
            </tr>
            <tr>
                <td><textarea cols="55" rows="4" name="comm"><%=refdto.getComm() %></textarea></td>
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