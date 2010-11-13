<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Collection" %>   
<jsp:useBean id="samplingunitsingle" scope="request" class="org.tgdb.samplingunit.samplingunitmanager.SamplingUnitDTO" />
<%
    org.tgdb.form.FormDataManager fdm = (org.tgdb.form.FormDataManager)request.getAttribute("formdata");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>Edit Sampling unit</title>
        <script language="JavaScript" src="javascripts/layers.js"></script>
    </head>
    <body>
        <h1>Sampling unit</h1>
        
        <m:window name="su.main" title="<%="Sampling Unit "+samplingunitsingle.getName()%>" workflow="ViewSamplingUnit" state="expanded">
            <menu>
                <m:hide privilege="SU_W"> 
                    <a href="Controller?workflow=EditSamplingUnit&suid=<jsp:getProperty name="samplingunitsingle" property="suid"/>"><m:img name="edit" title="Edit this sampling unit"/></a><br>
                    <a href="Controller?workflow=RemoveSamplingUnit&suid=<jsp:getProperty name="samplingunitsingle" property="suid"/>" onClick="return confirm('Remove sampling unit?')"><m:img name="delete" title="Remove this sampling unit"/></a><br>
                </m:hide>
                <br>
                <a href="Controller?workflow=CheckSamplingUnit&suid=<jsp:getProperty name="samplingunitsingle" property="suid"/>"><m:img name="check" title="Check this sampling unit for incorrect individuals"/></a><br>
            </menu>
            <body>
                <table>
                    <tr class="block">
                        <td class="block">
                            <b>Name</b><br> <jsp:getProperty name="samplingunitsingle" property="name"/>
                        </td>
                        <td class="block">
                            <b>Species</b><br> <jsp:getProperty name="samplingunitsingle" property="sname"/>
                        </td> 
                        <td class="block">
                            <b>Status</b><br><jsp:getProperty name="samplingunitsingle" property="status"/>
                        </td>                         
                    </tr>
                    <tr class="block">
                        <td class="block" colspan="3">
                            <b>Comment</b><br><jsp:getProperty name="samplingunitsingle" property="comm"/>
                        </td>            
                    </tr>      
                    <tr class="block">  
                        <td class="block" colspan="3">
                            <b>Misc</b><br>Exists in <jsp:getProperty name="samplingunitsingle" property="numProjects"/> project(s) and includes <jsp:getProperty name="samplingunitsingle" property="inds"/> individuals and 
                            <jsp:getProperty name="samplingunitsingle" property="numModels"/> models.
                        </td>        
                    </tr> 
                    <tr class="block">
                        <td class="block">
                            <b>User</b><br> <jsp:getProperty name="samplingunitsingle" property="usr"/>
                        </td>          
                        <td class="block">
                            <b>Last updated</b><br><jsp:getProperty name="samplingunitsingle" property="ts"/>
                        </td>        
                    </tr>
                </table>
            </body>
        </m:window>
        
        
        <br>        
        <form action="Controller" method="post">      
            
            <m:window title="Resources" name="su.resources2" workflow="ViewSamplingUnit">
                <menu>
                    <a href="Controller?workflow=AddLinkResourceSU&suid=<jsp:getProperty name="samplingunitsingle" property="suid"/>"><m:img name="bookmark_add" title="Add Link"/></a><br>
                    <a href="Controller?workflow=AddFileResourceSU&suid=<jsp:getProperty name="samplingunitsingle" property="suid"/>"><m:img name="add" title="Add File"/></a><br>
                </menu>
                <body>
                    <m:resource-simple-list resourceTreeCollection="resourceTree" projectId='<%=(String)request.getAttribute("pid")%>'/>
                </body>
            </m:window>
            
            <br>
          
            <m:window title="History" name="su.history2" workflow="ViewSamplingUnit">
                <menu></menu>
                <body>
                    <table class="block_data">  
                        <tr>
                            <th colspan="5" class="data" align="center">Current data</th>
                        </tr>
                        <tr>
                            <th class="data">Name</th>
                            <th class="data">Status</th>
                            <th class="data">Comment</th>
                            <th class="data">User</th>
                            <th class="data">Last updated</th>
                        </tr>
                        <tr>
                            <td><jsp:getProperty name="samplingunitsingle" property="name"/></td>
                            <td><jsp:getProperty name="samplingunitsingle" property="status"/></td>
                            <td><jsp:getProperty name="samplingunitsingle" property="comm"/></td>
                            <td><jsp:getProperty name="samplingunitsingle" property="usr"/></td>
                            <td><jsp:getProperty name="samplingunitsingle" property="ts"/></td>
                        </tr>
                        <tr>
                            <th colspan="5" class="data" align="center">History</th>
                        </tr>                    
                        <m:iterate-collection collection="samplingunitlog">

                            <tr class="#?alt#">
                                <td>#:getName#</td>
                                <td>#:getStatus#</td>
                                <td>#:getComm#</td>
                                <td>#:getUsr#</td>
                                <td>#:getTs#</td>
                            </tr>

                        </m:iterate-collection>                
                    </table>   
                </body>           
            </m:window>
            
                
        </form> 
    </body>
</html>
