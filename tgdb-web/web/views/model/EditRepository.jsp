<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
org.tgdb.model.modelmanager.RepositoriesDTO repodto = (org.tgdb.model.modelmanager.RepositoriesDTO)request.getAttribute("repository");
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; edit repository</title>
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
            <span class="header_01">Edit Repository</span>
            <table>
                <tr>
                    <td><b>Repository</b></td>
                </tr>
                <tr>
                    <td>
                        <input type="text" name="reponame" value='<jsp:getProperty name="repository" property="reponame"/>' size="35"/>
                    </td>
                </tr>
                <tr>
                    <td><b>hasdb?</b></td>
                </tr>
                <tr>
                    <td>
                        <m:checkbox collection="hasdbs" name="hasdb" idGetter="toString" textGetter="toString" selected="<%=repodto.getHasdb()%>"/>
                    </td>
                </tr>
                <tr>
                    <td><b>mouseurl</b></td>
                </tr>
                <tr>
                    <td>
                        <input type="text" name="mouseurl" value='<jsp:getProperty name="repository" property="mouseurl"/>' size="35"/>
                    </td>
                </tr>
                <tr>
                    <td><b>repourl</b></td>
                </tr>
                <tr>
                    <td>
                        <input type="text" name="repourl" value='<jsp:getProperty name="repository" property="repourl"/>' size="35"/>
                    </td>
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
