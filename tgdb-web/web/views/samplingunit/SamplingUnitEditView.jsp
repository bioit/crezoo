<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="samplingunitsingle" scope="request" class="org.tgdb.samplingunit.samplingunitmanager.SamplingUnitDTO" />
<%
    org.tgdb.samplingunit.samplingunitmanager.SamplingUnitDTO dto = (org.tgdb.samplingunit.samplingunitmanager.SamplingUnitDTO)request.getAttribute("samplingunitsingle");
    String status = dto.getStatus();
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <script language="JavaScript" src="javascripts/validate.js"></script>
        <script language="JavaScript">
            function init() { 
                define('name', 'string', 'Name', 1, 20);
                define('comm', 'string', 'Comment', null, 256);                
            }        
        </script>          
        <title>Edit Sampling unit</title>
    </head>
    <body onLoad="init()">
        <h1>Edit Sampling unit</h1>
        <form action="Controller" method="post">
            <p>
                <input type="hidden" name="suid" value='<jsp:getProperty name="samplingunitsingle" property="suid"/>'/>
                <table>
                    <tr>
                        <td>Species: <jsp:getProperty name="samplingunitsingle" property="sname"/></td>
                    </tr>
                    <tr>
                        <td>Included in: <jsp:getProperty name="samplingunitsingle" property="numProjects"/> projects</td>
                    </tr>                    
                    <tr>
                        <td>Last updated by: <jsp:getProperty name="samplingunitsingle" property="usr"/>, <jsp:getProperty name="samplingunitsingle" property="ts"/></td>
                    </tr> 
                   <tr>
                        <td>Name</td>
                    </tr>
                    <tr>
                        <td><input type="text" name="name" size="35" value='<jsp:getProperty name="samplingunitsingle" property="name"/>'/></td>
                    </tr>
                    <tr>                    
                        <td>Status</td>
                    </tr>
                    <tr>
                        <td>
                            <m:status value="<%=status%>"/>
                        </td>
                    </tr>                    
                    <tr>
                        <td>Comment</td>
                    </tr>
                    <tr>
                        <td><textarea type="text" cols="35" rows="6" name="comm"><jsp:getProperty name="samplingunitsingle" property="comm"/></textarea></td>
                    </tr>                    
                </table>
            </p>
            <m:save onClick="validate();return returnVal;" cancelName="back" saveName="save"/>
        </form> 
    </body>
</html>
