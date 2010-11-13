<%
    org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller");
%>  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>List Of Backcrosses</title>
    </head>
    <body>         
        <h1>List Of Backcrosses</h1>
        
        <form action="Controller" method="post">
        <hr id="ruler">
            <table class="data">
                <tr>
                    <th class="data" width="8%">MMMDb#</th>
                    <th class="data" width="10%">Institution</th>
                    <th class="data" width="16%">Common Line name</th>
                    <th class="data" width="16%">Mutation(s)</th>
                    <th class="data" width="15%">Research Application</th>
                    <th class="data" width="15%">Backcr. Strain</th>
                    <th class="data" width="10%">Backcrosses</th>
                </tr>
                <m:iterate-collection collection="modelsdto">
                    <tr class="#?alt#">
                        <td width="8%">#:getAccNr#</td>
                        <td width="10%">#:getGroupName#</td>
                        <td width="16%"><a href="Controller?workflow=ViewModel&expand_all=true&name_begins=model.block&eid=#:getEid#">#:getLineName#</a></td>
                        <td width="16%">#:getMutations#</td>
                        <td width="15%">#:getResearchAppType#</td>
                        <td width="15%">#:getBackcrossingStrain#</td>
                        <td width="10%">#:getBackcrossesNumber#</td>
                    </tr>
                </m:iterate-collection>
            </table>
       </form>
    </body>
</html>
