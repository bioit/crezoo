<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="user" scope="request" type="org.tgdb.project.projectmanager.UserDTO" />
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; trangene</title>
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
            <span class="header_01"><jsp:getProperty name="user" property="name"/></span>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                    <m:hide privilege="PROJECT_ADM">
                        <a href="Controller?workflow=EditUser&amp;id=<jsp:getProperty name="user" property="id"/>">Edit</a>
                    </m:hide>
                    <input type="submit" name="back" value="back" title="Back" />
                    </p>
                </td>
            </tr>
        </table>
    <table>
        <tr>
            <td>
                <b>E-mail:</b>&nbsp;<a href="mailto:<jsp:getProperty name='user' property='email'/>" class="data_link"><jsp:getProperty name='user' property='email'/></a>
            </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr>
            <td>
                <b>Personal Website:</b>&nbsp;<a href="<jsp:getProperty name='user' property='userLinkUrl'/>" target="_blank" class="data_link"><jsp:getProperty name='user' property='userLinkName'/></a>
            </td>           
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr>
            <td>
                <b>Research Group:</b>&nbsp;<jsp:getProperty name="user" property="groupName"/>
            </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr>
            <td>
                <b>Group Website:</b>&nbsp;<a href="<jsp:getProperty name='user' property='groupLinkUrl'/>" target="_blank" class="data_link"><jsp:getProperty name='user' property='groupLinkName'/></a>
            </td>                  
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr>
            <td>
                <b>Group Address:</b>&nbsp;<jsp:getProperty name="user" property="groupAddress"/>
            </td>        
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr>
            <td>
                <b>Group Phone</b>&nbsp;<jsp:getProperty name="user" property="groupPhone"/>
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
