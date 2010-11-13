<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%
    //org.tgdb.project.projectmanager.ProjectDTO project;
    //project = (org.tgdb.project.projectmanager.ProjectDTO)session.getAttribute("project.projectdto");
    
    org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller");
    
    int adminmenu = 0;
    
    if (caller!= null && caller.getUsr().compareTo("admin13")==0){
        adminmenu = 1;
    }else{
        adminmenu = 0;
    }
    
  %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <META HTTP-EQUIV="Window-target" CONTENT="_top">
        <title>TgDb [transgenic mice database]</title>
        <link rel="SHORTCUT ICON" href="images/favicon.ico" type="image/x-icon">
    </head>
    
    <script language= "JavaScript">
    <!--Break out of frames
        if (top.frames.length!=0)
            top.location=self.document.location;
    //-->
    </script>
    
    <frameset rows="108,*,30" FRAMEBORDER=0 FRAMESPACING=0 BORDER=0>
        <frameset cols="50%,50%" FRAMEBORDER=0 FRAMESPACING=0 BORDER=0>
            <frame name="top" src="top.jsp" SCROLLING=NO MARGINWIDTH=0 MARGINHEIGHT=0>
            <frame name="topright" src="topright.jsp" SCROLLING=NO MARGINWIDTH=0 MARGINHEIGHT=0>
        </frameset>
        <%--frame name="test" src="CloseWindowController.jsp" SCROLLING=NO MARGINWIDTH=0 MARGINHEIGHT=0--%>
        <% if (adminmenu == 1){ %>
        <frameset cols="155,*" FRAMEBORDER=0 FRAMESPACING=0 BORDER=0>
            <frame name="nav" src="Menu">
            <frame name="page" src="Controller?workflow=boot">
        </frameset>
        <% } else {%>
        <frame name="page" src="Controller?workflow=boot">
        <% } %>
        <frame name="bottom" src="Bottom" SCROLLING=NO MARGINWIDTH=0 MARGINHEIGHT=0>
    </frameset>
    <noframes>
    <body>
        <p>This page uses frames, but your browser doesn't support them. Try <a href="http://www.mozilla.com/en-US/firefox/"><b>Mozilla Firefox</b></a> instead.</p>
    </body>
    </noframes> 
</html>
