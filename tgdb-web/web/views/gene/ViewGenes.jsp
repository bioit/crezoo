<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; trangene index</title>
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
            <span class="header_01">Transgene Index</span>
            <table>
                <tr>
                    <td>
                      <m:navigation-buttons workflow="ViewGenes" showText='true'/>
                    </td>
                </tr>
            </table>
            <table class="data">
                <tr>                    
                    <th class="data" width="70%"><span>Transgene Name / Symbol</span></th>
                    <th class="data" width="15%"><span>Related Mice</span></th>
                    <th class="data"><span>Last Update</span></th>
                </tr>
                <m:iterate-collection collection="genes">
                    <tr class="#?alt#">
                        <td width="70%"><a href="Controller?workflow=ViewGene&amp;gaid=#:getGaid#" title="View Transgene">#:getName# / #:getGenesymbol#</a></td>
                        <td width="15%"><a href="Controller?workflow=ViewModels&amp;_gaid=#:getGaid#">#:getModels_num#</a></td>
                        <td>#:getTs#</td>
                    </tr>
                </m:iterate-collection>
            </table>
            <table>
                <tr>
                    <td>
                      <m:navigation-buttons workflow="ViewGenes"/>
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