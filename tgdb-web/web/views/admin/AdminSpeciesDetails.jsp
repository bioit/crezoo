<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>Species View</title>
    </head>
    <body>
    <jsp:useBean id="species" scope="request" type="org.tgdb.adminmanager.SpeciesDTO" />

        <h1>Species</h1>
        <table class="block">
            <th class="block" colspan="3">
                <m:hide privilege=""> <!-- Endast admin -->
                    <a href="Controller?workflow=AdminEditSpecies&sid=<jsp:getProperty name="species" property="sid"/>">
                    <m:img name="edit"/></a>
                    <a href="Controller?workflow=AdminRemoveSpecies&sid=<jsp:getProperty name="species" property="sid"/>">
                    <m:img name="delete"/></a>
                    
                </m:hide>
                &nbsp;
            </th>
            <tr>
                <td class="block">
                    <b>Name</b><br> <jsp:getProperty name="species" property="name"/>
                </td>
                <td class="block">
                    <b>Comment</b><br><jsp:getProperty name='species' property='comm'/>
                </td> 
            </tr>
        </table>   
        <form action="Controller" method="post">
            <br>
            <m:back/>
        </form>
    </body>
</html>
