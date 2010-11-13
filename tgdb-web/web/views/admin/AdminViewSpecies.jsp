
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>

    <h1>Species</h1>
    
    <form action="Controller" method="post">
        <table class=data>
            <tr class=data>
                <th class=data>Name</th>
                <th class=data>Comment</th>
                <th class=data>Details</th>
            </tr>
            <m:iterate-collection collection="species">
                <tr class="data #?alt#">
                    <td>#:getName#</td>
                    <td>#:getComm#</td>
                    <td><a href="Controller?workflow=AdminSpeciesDetails&sid=#:getSid#"><m:img name="view"/></a></td>
                </tr>
            </m:iterate-collection>
        </table>
    </form>
    </body>
</html>
