
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Collection" %>

<%
    org.tgdb.frame.Navigator nav = (org.tgdb.frame.Navigator)session.getAttribute("navigator"); 
    Collection dto = (Collection)request.getAttribute("sucheckdto");
    String ok = (String)request.getAttribute("indsOK");
    String err = (String)request.getAttribute("indsERR");
    String mess = "<b>Individuals ok:</b> "+ok+"<br><b>Individuals with errors:</b> "+err;
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>Check sampling unit</title>
    </head>
    <body>                          
        <h1>Check sampling unit</h1>
        <p><%=mess%></p>
        <hr id="ruler">
        <table class="data">        
        <tr>
            <th class="data">Identity</th>
            <th class="data">F. not male</th>
            <th class="data">F. disabled</th>
            <th class="data">F. too young</th>
            <th class="data">M. not female</th>
            <th class="data">M. disabled</th>
            <th class="data">M. too young</th>
        </tr>
        <m:iterate-collection collection="<%=dto%>" >
        
        <tr class="#?alt#">
            <td align="center">#:getIdentity#</td>
            <td align="center">#:getFNotMale#</td>
            <td align="center">#:getFDisabled#</td>
            <td align="center">#:getFTooYoung#</td>
            <td align="center">#:getMNotFemale#</td>
            <td align="center">#:getMDisabled#</td>
            <td align="center">#:getMTooYoung#</td>            
        </tr>
        
        </m:iterate-collection>
        </table> 
       <form>
            <p>                                
                <input id="button" type="submit" name="back" value="Back">
            </p>
       </form>
    </body>
</html>