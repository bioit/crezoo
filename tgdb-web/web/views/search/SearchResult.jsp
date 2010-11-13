<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>

        <table>
            <tr valign="bottom">
                <td class="head">Search Results</td>
            </tr>
        </table>
        <hr id="ruler">
        <table>
            <tr><td>number of hits: <%=request.getAttribute("hits")%></td></tr>
        </table>
    
            <table class="data">
                <tr>
                    <th class="data" width="20%">Accession nr.</th>
                    <th class="data" width="20%">Line name</th>
                    <th class="data" width="15%">Contact</th>
                    <th class="data" width="15%">User</th>
                    <th class="data" width="15%">Updated</th>
                    <th class="data" width="15%">Details</th>
                </tr>
                <m:iterate-collection collection="models">
                    <tr class="#?alt#">
                        <td width="20%">#:getAccNr#</td>
                        <td width="20%">#:getLineName#</td>
                        <td width="15%"><a href="Controller?workflow=ViewUser&id=#:getContactId#" title="View the details for this user">#:getContactName#</a></td>
                        <td width="15%"><a href="Controller?workflow=ViewUser&id=#:getUserId#" title="View the details for this user">#:getUser#</a></td>
                        <td width="15%">#:getTs#</td>                        
                        <td width="15%"><a href="Controller?workflow=ViewModel&eid=#:getEid#"><m:img name="view" title="View the details for this model"/></a></td>
                    </tr>
                </m:iterate-collection>
            </table> 
    
        
    </body>
</html>
