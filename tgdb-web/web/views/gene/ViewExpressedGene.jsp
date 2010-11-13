<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="gene" scope="request" type="org.tgdb.model.modelmanager.GeneDTO" />
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; view expressed gene</title>
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
            <span class="header_01">View Expressed Gene</span>
            <table>
                <tr>
                    <td><b>Expressed Gene Name</b></td>
                </tr>
                <tr>
                    <td><jsp:getProperty name="gene" property="name_ss"/></td>
                </tr>
                <tr>
                    <td><b>Expressed Gene Symbol</b></td>
                </tr>
                <tr>
                    <td><jsp:getProperty name="gene" property="genesymbol_ss"/></td>
                </tr>
                <tr>
                    <td><b>Chromosome</b></td>
                </tr>
                <tr>
                    <td><jsp:getProperty name="gene" property="chromoName"/></td>
                </tr>
                <tr>
                    <td><b>MGI ID</b></td>
                </tr>
                <tr>
                    <td><jsp:getProperty name="gene" property="mgiurl"/></td>
                </tr>
                <tr>
                    <td><b>Comment</b></td>
                </tr>
                <tr>
                    <td><jsp:getProperty name="gene" property="comm"/></td>
                </tr>
            </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
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
