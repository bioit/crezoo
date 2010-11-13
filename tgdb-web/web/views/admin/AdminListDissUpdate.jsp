<%
    org.tgdb.form.FormDataManager fdm = (org.tgdb.form.FormDataManager)request.getAttribute("formdata");
    org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller");
%>  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>Mice That Need Dissemination Level Update</title>
    </head>
    <body>         
        <h1>Mice That Need Dissemination Level Update</h1>
        
        <form action="Controller" method="post">
        <p class="toolbar">
            <input type="image" src="images/icons/nav_left_blue_24.png" name="reset" value="Reset">
            <a href="Controller?workflow=DisseminationUpdate&models.help=true"><m:img name="info_24" title="Help & Info for this page"/></a>
        </p>

        
        <table>
            <tr valign="bottom">
                <td>Sort By</td>
                <td>Research Applications</td>
                <td>Researcher</td>
                <td>Institution</td>
                <td>Mutation Type</td>
            </tr>
            <tr valign="top">
                <td><m:checkbox collection="sortby" onChange="this.form.submit()" name="ordertype" idGetter="toString" textGetter="toString" selected="<%=fdm.getValue("ordertype")%>" wildcardOption="true"/></td>
                <td><m:checkbox collection="rapps" onChange="this.form.submit()" name="raid" idGetter="getId" textGetter="getName" selected="<%=fdm.getValue("raid")%>" wildcardOption="true"/></td>
                <td><m:checkbox collection="participantnames" onChange="this.form.submit()" name="participantname" idGetter="toString" textGetter="toString" selected="<%=fdm.getValue("participantname")%>" wildcardOption="true"/></td>
                <td><m:checkbox collection="participants" onChange="this.form.submit()" name="groupname" idGetter="toString" textGetter="toString" selected="<%=fdm.getValue("groupname")%>" wildcardOption="true"/></td>
                <td><m:checkbox collection="mutations" onChange="this.form.submit()" name="mutationtypes" idGetter="getId" textGetter="getName" selected="<%=fdm.getValue("mutationtypes")%>" wildcardOption="true"/></td>
            </tr>
            <m:hide privilege="MODEL_W" suid="<%=caller.getSuid()%>">
            <tr>
                <%--<td>Dissemination</td>--%>
                <m:hide-collection collection="samplingunits" limit="1">
                    <td>Sampling Unit</td>
                </m:hide-collection>
            </tr>
            <tr>
                <%--<td><m:checkbox collection="disseminationlevels" onChange="this.form.submit()" name="disslevel" idGetter="toString" textGetter="toString" selected="<%=fdm.getValue("disslevel")%>" wildcardOption="true"/></td>--%>
                <td><m:su-combobox hideEmpty="yes" onChange="this.form.submit()"/></td>
            </tr>
            </m:hide>
        </table>
       
        <hr id="ruler">  
            <table>
                <tr>
                    <td>
                      <m:navigation-buttons workflow="DisseminationUpdate" showText='true'/>  
                    </td>
                </tr>
            </table>
            <table class="data">
                <tr>
                    <th class="data" width="13%">MMMDb ID</th>
                    <th class="data" width="29%">Common Line name</th>
                    <th class="data" width="28%">Mutation(s)</th>
                    <th class="data" width="20%">Research Application</th>
                    <th class="data" width="10%">Updated</th>
                    <%--<th class="data" width="18%">Contact</th>
                    <th class="data" width="15%">User</th>
                    <th class="data" width="10%">Institution</th>
                    <th class="data" width="5%">Details</th>--%>
                </tr>
                <m:iterate-collection collection="modelsdto">
                    <tr class="#?alt#">
                        <td width="13%">#:getAccNr#</td>
                        <td width="29%"><a href="Controller?workflow=ViewModel&expand_all=true&name_begins=model.block&eid=#:getEid#">#:getLineName#</a></td>
                        <td width="28%">#:getMutations#</td>
                        <td width="20%">#:getResearchAppType#</td>
                        <td width="10%">#:getTs#</td>
                        <%--<td width="5%"><a href="Controller?workflow=ViewModel&eid=#:getEid#"><m:img name="view" title="View the details for this model"/></a></td>
                        <td width="18%"><a href="Controller?workflow=ViewUser&id=#:getContactId#" title="View the details for this user">#:getContactName#</a></td>
                        <td width="15%"><a href="Controller?workflow=ViewUser&id=#:getUserId#" title="View the details for this user">#:getUser#</a></td>
                        <td width="10%">#:getGroupName#</td>--%>
                    </tr>
                </m:iterate-collection>
            </table>
            <table>
                <tr>
                    <td>
                      <m:navigation-buttons workflow="DisseminationUpdate"/>  
                    </td>
                </tr>
            </table>
       </form>
    </body>
</html>
