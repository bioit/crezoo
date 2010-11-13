<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="straindto" scope="request" type="org.tgdb.model.modelmanager.StrainDTO" />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title></title>
    </head>
    <body>

    <h1>Strain <a href="Controller?workflow=ViewStrain&eid=<%=straindto.getStrainId()%>&strain.info=true"><m:img name="info"/></a></h1>
    <m:hide-block name="strain.info">
        <table class="info">
            <td>
                Strain info...
            </td>
        </table>
    </m:hide-block>
      
    <form action="Controller" method="post">
        
        <table class="block">
            <th class="block" colspan="2">
                
                    <a href="Controller?workflow=EditStrain&strainid=<jsp:getProperty name="straindto" property="strainId"/>"><m:img name="edit" title="Edit this Strain"/></a>
                    <a href="Controller?workflow=RemoveStrain&strainid=<jsp:getProperty name="straindto" property="strainId"/>" title="Delete this strain" onClick="return confirm('Remove strain? ')"><m:img name="delete"/></a>
                
                &nbsp;
            </th>
            <tr class="block">
                <td class="block">
                    <b>Designation</b><br> <jsp:getProperty name="straindto" property="designation"/>
                </td>
                <td class="block">
                    <b>Type</b><br> <jsp:getProperty name="straindto" property="strainTypeNames"/>
                </td>            
            </tr>
            <tr class="block">
                <td class="block">
                    <b>State</b><br><jsp:getProperty name="straindto" property="strainStateNames"/>
                </td>
                <td class="block">
                
                </td>                
            </tr>
        </table>
        <br>
        
        <table class="block">
            <th class="block">            
               <a href="Controller?workflow=ViewStrain&strain.something_display=true" title="Expand/Collapse this section">Something...</a>
            </th>
            <tr>
            <td>
                <m:hide-block name="strain.something_display">
 
                    xyz
                   
                </m:hide-block>
            </td>
            </tr>
        </table>           
        
        <p>
            <m:back/>
        </p>
    </form>
    </body>
</html>
