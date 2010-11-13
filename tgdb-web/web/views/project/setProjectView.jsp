
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
   
<jsp:useBean id="navigator" type="org.tgdb.frame.Navigator" scope="session" />
<jsp:useBean id="caller" type="org.tgdb.TgDbCaller" scope="session" />
<%--<jsp:useBean id="projects" class="java.util.Collection" scope="request" />--%>
<%
    java.util.Collection projects = (java.util.Collection)request.getAttribute("projects");
    java.util.Collection samplingunits = (java.util.Collection)request.getAttribute("samplingunits");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <!--
        <script language="JavaScript" src="javascripts/validate.js"></script>
        <script language="JavaScript">
            function init() { define('rows', 'num', 'Rows on page'); }        
        </script>        
        -->
        
        <title>Set project</title>        
    </head>
    <body> <!-- id="bodyChromo" -->
        <!--  onLoad="init()"  -->
        <h1>Project settings</h1>
        <table class="info">
            <td class="info">
                Select the project you would like to work on and press the <b>Set</b> button. 
                You can also select the maximum number of rows with data to display in listings by
                changing the value for <b>Rows on page</b>.
            </td>
        </table>
        <!-- document.getElementById('js_enabled').value = 'true' ; -->
        <!--  onsubmit='parent.frames[0].location = "NavMenu"'-->
        <form action="Controller" method="post">
            <!--<input type="hidden" name="js_enabled" id="js_enabled" value="false" />-->
            <table>
                <tr>
                    <td><label>Project</label><br>
                        <m:checkbox onChange="parent.frames.nav.location = 'Menu';" collection="projects" name="project" idGetter="getPid" textGetter="getName" selected='<%=caller.getPid()%>' />
                    </td>
                </tr>
                <tr>
                    <td>
                        <br>
                    </td>
                </tr>
                <tr>
                    <td><label>Rows on page</label><br>
                    <input type="text" name="rows" size="15" value="<%=navigator.getPageManager().getDelta() %>"</td>
                </tr>
                <tr>
                    <td align="right">
                        <input type="submit" id="button" value="Set" onClick="parent.frames.nav.location = 'Menu';">
                    </td>
                </tr>
            </table>
        </form>      
    </body>
</html>
