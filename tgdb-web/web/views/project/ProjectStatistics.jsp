<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="stats" scope="request" type="org.tgdb.project.projectmanager.ProjectStatisticsDTO"/>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>

    <h1>Project Statistics</h1>
    
    <table>
        <tr>
            <td>Project Name</td>
            <td><jsp:getProperty name="stats" property="name"/></td>
        </tr>
        <tr>
            <td>Comment</td>
            <td><jsp:getProperty name="stats" property="comm"/></td>
        </tr>
        <tr>
            <td colspan="2"><hr></td>
        </tr>
        <tr>
            <td>Users</td>
            <td><jsp:getProperty name="stats" property="users"/></td>
        </tr>
        <tr>
            <td>Species</td>
            <td><jsp:getProperty name="stats" property="species"/></td>
        </tr>
        <tr>
            <td>Sampling Units</td>
            <td><jsp:getProperty name="stats" property="samplingunits"/></td>
        </tr>
        <tr>
            <td>Individuals</td>
            <td><jsp:getProperty name="stats" property="individuals"/></td>
        </tr>
        <tr>
            <td>Samples</td>
            <td><jsp:getProperty name="stats" property="samples"/></td>
        </tr>
        <tr>
            <td>Variables</td>
            <td><jsp:getProperty name="stats" property="variables"/></td>
        </tr>
        <tr>
            <td>Phenotypes</td>
            <td><jsp:getProperty name="stats" property="phenotypes"/></td>
        </tr>
        <tr>
            <td>Markers</td>
            <td><jsp:getProperty name="stats" property="markers"/></td>
        </tr>
        <tr>
            <td>Genotypes</td>
            <td><jsp:getProperty name="stats" property="genotypes"/></td>
        </tr>
    </table>
    </body>
</html>
