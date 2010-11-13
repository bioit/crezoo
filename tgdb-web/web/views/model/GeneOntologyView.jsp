<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="geneontologydto" scope="request" type="org.tgdb.model.modelmanager.GeneOntologyDTO" />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>Edit group</title>   
    </head>
    <body>
    <h1>Gene ontology</h1>
    <p>
        <a href="Controller?workflow=ViewModels">Models</a>&nbsp>>
        <a href="Controller?workflow=ViewModel">Model</a>&nbsp>>
        <a href="Controller?workflow=ViewGenMod">Genetic modification</a>&nbsp>>
        Gene ontology
    </p>    
    <table class="block">
        <th class="block" colspan="2">
            [<a href="Controller?workflow=EditGeneOntology&goid=<jsp:getProperty name="geneontologydto" property="goid"/>">Edit</a>]
            [<a href="Controller?workflow=RemoveGeneOntology&goid=<jsp:getProperty name="geneontologydto" property="goid"/>">Remove</a>]
        </th>
        <tr>
            <td>
                <b>GO name/process</b><br> <jsp:getProperty name="geneontologydto" property="name"/>
            </td>
        </tr>
        <tr>
            <td>
                <b>GO nr</b><br> <jsp:getProperty name="geneontologydto" property="linkid"/>
            </td>
        </tr>        
        <tr>
            <td>
                <b>Comment</b><br> <jsp:getProperty name="geneontologydto" property="comm"/>
            </td>            
        </tr>              
    </table> 
    </body>
</html>
