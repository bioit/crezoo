<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Collection" %>
<%  
    org.tgdb.frame.Navigator nav = (org.tgdb.frame.Navigator)session.getAttribute("navigator");     
    org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller");  
    org.tgdb.project.ParamDataObject pdo = (org.tgdb.project.ParamDataObject)request.getAttribute("paramdataobject");    
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="arx" %>
        <title>JSP Page</title>
    </head>
    <body> 
        <h1>Sampling units</h1>
        <form action="Controller" method="post" name=>
        <table>
            <tr>
                <td>Species<br>
                    <m:checkbox collection="species" selected='<%=caller.getSid()%>' name='sid' idGetter='getSid' textGetter='getName'/>
                    <input id="button" type="submit" value="Set">
                </td>
                <td>Status <br>
                    <m:status value='<%=pdo.getValue("status")%>'/>                    
                </td>
            </tr>
            <tr>
                <td colspan=2 align="right">
                    <input id="button" type="submit" name="collect.getsufullaction.display" value="Display">
                    <input id="button" type="submit" name="reset" value="Reset">
                </td>
            </tr>
        </table> 
       <table>
            <tr>
                <td>
                    <b>Note: This view currently only includes Individuals and not models that are attached to the Sampling unit!</b>
                </td>
            </tr>       
       </table>
        <hr id="ruler">
        <m:navigation-buttons workflow="ViewSamplingUnits" showText='true'/>
        <table class="data">
        <tr>
            <th class="data">Name</th>
            <th class="data">Comment</th>
            <th class="data">Individuals/Models</th>
            <th class="data">User</th>
            <th class="data">Updated</th>
            <th class="data">Details</th>
        </tr>
        <m:iterate-collection collection="samplingunits">
        
        <tr class="#?alt#">
            <td>#:getName#</td>
            <td>#:getComm#</td>
            <td>
                <a href="Controller?workflow=ViewIndividuals&suid=#:getSuid#&collect.getindividualsaction.display=go" title="View a listing of the individuals that are attached to this sampling unit">#:getInds#</a>
                <a href="Controller?workflow=ViewModels&suid=#:getSuid#&collect.getmodelsaction.display=go" title="View a listing of the models that are attached to this sampling unit">#:getNumModels#</a>
            </td>
            <td>#:getUsr#</td>
            <td>#:getTs#</td>
            <td><a href="Controller?workflow=ViewSamplingUnit&suid=#:getSuid#"><m:img name="view" title="View details for this sampling unit"/></a></td>                   
            
        </tr>
        
        </m:iterate-collection>
        </table> 
       <m:navigation-buttons workflow="ViewSamplingUnits"/>
       </form>
    </body>
</html>
