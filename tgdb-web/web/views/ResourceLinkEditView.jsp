<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="linkdto" scope="request" class="org.tgdb.resource.resourcemanager.LinkDTO" />
<jsp:useBean id="resource" scope="request" class="org.tgdb.resource.resourcemanager.ResourceDTO" />
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; resource link edit</title>
    <jsp:include page="/Header"/>
    <link rel="stylesheet" type="text/css" href="css/crezoo-tabs.css" />
    <script type="text/javascript" src="javascripts/validate.js"></script>
    <script type="text/javascript">
            function init() {
                define('name', 'string', 'Name', null, 500);
                define('url', 'string', 'URL', null, 500);
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
        <span class="header_01">Resource Link Edit</span>
        <input type="hidden" name="resourceId" value='<jsp:getProperty name="resource" property="resourceId"/>'/>
        <input type="hidden" name="id" value='<%=(String)request.getAttribute("id")%>'/>
        <table>
            <tr>
                <td><b>Name</b></td>
            </tr>
            <tr>
                <td><input type="text" name="name" size="55" maxlength="500" value='<jsp:getProperty name="resource" property="resourceName"/>'/></td>
            </tr>
            <tr>
                <td><b>URL</b></td>
            </tr>
            <tr>
                <td><input type="text" name="url" size="55" maxlength="500" value='<jsp:getProperty name="resource" property="resourceLink"/>'/></td>
            </tr>
            <tr>
                <td><b>Category</b></td>
            </tr>
            <tr>
                <td><m:checkbox name="catid" collection="categories" idGetter="getCatId" textGetter="getCatName" selected="<%=resource.getCategoryId()%>"/></td>
            </tr>
            <tr>
                <td><b>Comment</b></td>
            </tr>
            <tr>
                <td><textarea cols="55" rows="4" name="comm"><jsp:getProperty name="resource" property="resourceComment"/></textarea></td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <input type="submit" name="upload" value="Save" title="Save" onclick="validate();return returnVal;"/>
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