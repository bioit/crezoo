<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>crezoo &mdash; strain index</title>
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
                        <span class="header_01">Strain Index</span><table>
                <tr>
                    <td>
                      <m:navigation-buttons workflow="ViewStrains" showText='true'/>
                    </td>
                </tr>
            </table>
                        <table class="data">
                            <tr>
                                <th class="data" width="20%"><span>ID</span></th>
                                <th class="data"><span>Designation</span></th>
                                <th class="data" width="20%"><span>Related Mice</span></th>
                            </tr>
                            <m:iterate-collection collection="strains">
                                <tr class="#?alt#">
                                    <td width="20%">#:getStrainId#</td>
                                    <td><a href="Controller?workflow=ViewStrain&amp;strainid=#:getStrainId#">#:getDesignation_ss#</a></td>
                                    <td width="20%"><a href="Controller?workflow=ViewModels&amp;_strain=#:getStrainId#" title="View related mice">#:getModels#</a></td>
                                </tr>
                            </m:iterate-collection>
                        </table><table>
                <tr>
                    <td>
                      <m:navigation-buttons workflow="ViewStrains"/>
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